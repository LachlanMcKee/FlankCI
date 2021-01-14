let branchSelectDropDown = document.getElementById('branch-select');
let buildSelectDropDown = document.getElementById('build-select');

async function load() {
    let branchData = await (await fetch('http://127.0.0.1:8080/ci-data')).json();

    let branches = branchData.branches;
    console.log("Branch data loaded:");
    console.log(branchData);

    let option;
    for (let i = 0; i < branches.length; i++) {
        option = document.createElement('option');
        option.text = branches[i];
        option.value = branches[i];
        branchSelectDropDown.add(option);
    }

    let defaultBranch = document.getElementById('defaultBranch');
    if (defaultBranch != null) {
        branchSelectDropDown.value = defaultBranch.value;
    }

    onBranchChanged(branchData);
    branchSelectDropDown.addEventListener("change", function () {
        onBranchChanged(branchData);
    });

    buildSelectDropDown.addEventListener("change", function () {
        onBuildChanged(branchData.branchBuilds[branchSelectDropDown.value]);
    });
}

function onBranchChanged(branchData) {
    document.querySelectorAll('#build-select option').forEach(option => option.remove());

    let builds = branchData.branchBuilds[branchSelectDropDown.value];
    console.log("Value changed: " + branchSelectDropDown.value + ". Builds:");
    console.log(builds);

    let option;
    for (let i = 0; i < builds.length; i++) {
        option = document.createElement('option');
        option.text = "[#" + builds[i].buildNumber + " / " + builds[i].status + "] - " + builds[i].commitMessage;
        option.value = builds[i].buildSlug;
        buildSelectDropDown.add(option);
    }

    onBuildChanged(builds);
}

async function onBuildChanged(buildsData) {
    document.getElementById('artifact-details').textContent = "Loading...";

    let buildSlug = buildSelectDropDown.value;

    let buildData = buildsData.find(x => x.buildSlug === buildSlug);

    let commitHashInput = document.getElementById('commit-hash');
    commitHashInput.value = buildData.commitHash;

    let buildSlugInput = document.getElementById('build-slug');
    buildSlugInput.value = buildData.buildSlug;

    let artifactData = await (await fetch('http://127.0.0.1:8080/artifact-data/' + buildSlug)).json();
    console.log("Artifact data:");
    console.log(artifactData);

    document.getElementById('artifact-details').innerHTML = artifactData.data.map(x => x.title + " (" + x.slug + ")").join("<br />");

    let androidTestArtifactData = artifactData.data.find(x => x.artifact_meta !== undefined && x.artifact_meta.build_type == "androidTest");
    let androidTestArtifactSlug = androidTestArtifactData.slug;
    console.log("Test apk artifact:");
    console.log(androidTestArtifactData);
    console.log("Test apk slug: " + androidTestArtifactSlug);

    let loadDataButton = document.getElementById('load-test-data-button');
    if (loadDataButton != null) {
        loadDataButton.addEventListener("click", function () {
            onTestDataRequested(buildSlug, androidTestArtifactSlug);
        });
    }
}

async function onTestDataRequested(buildSlug, artifactSlug) {
    let testData = await (await fetch('http://127.0.0.1:8080/test-apk-metadata/' + buildSlug + '/' + artifactSlug)).json();
    console.log("Test data:");
    console.log(testData);

    setupTestInputs(testData, new Set());
}

function setupTestInputs(testData, selectedAnnotationsSet) {
    console.log("============")
    console.log("setupTestInputs")
    console.log(selectedAnnotationsSet)
    console.log("============")

    let rootPackageInput = document.getElementById('root-package');
    let annotationsFieldSet = document.getElementById('annotations-field-set');
    let packagesFieldSet = document.getElementById('packages-field-set');
    let classesFieldSet = document.getElementById('classes-field-set');

    while (annotationsFieldSet.hasChildNodes()) {
        annotationsFieldSet.removeChild(annotationsFieldSet.lastChild);
    }

    while (packagesFieldSet.hasChildNodes()) {
        packagesFieldSet.removeChild(packagesFieldSet.lastChild);
    }

    while (classesFieldSet.hasChildNodes()) {
        classesFieldSet.removeChild(classesFieldSet.lastChild);
    }

    rootPackageInput.value = testData['rootPackage'];

    for (var i = 0; i < testData['annotations'].length; i++) {
        addElement(
            "annotation" + i,
            "annotation",
            testData['annotations'][i],
            annotationsFieldSet,
            selectedAnnotationsSet.has(testData['annotations'][i]),
            function (data) {
                return data
            },
            function (data, checked) {
                if (checked) {
                    selectedAnnotationsSet.add(data);
                } else {
                    selectedAnnotationsSet.delete(data);
                }
                setupTestInputs(testData, selectedAnnotationsSet);
            });
    }

    let testDataPackages = testData['packages'];
    var packagesAdded = 0;
    for (var i = 0; i < testDataPackages.length; i++) {
        let packageData = testDataPackages[i];
        if (selectedAnnotationsSet.size > 0) {
            if (packageData.annotationGroups == null || !checkIfAllAnnotationsFound(packageData.annotationGroups, selectedAnnotationsSet)) {
                continue;
            }
        }
        addElement("package" + i,
            "package",
            packageData,
            packagesFieldSet,
            false,
            function (data) {
                if (data.path.length > 0) {
                    return data.path
                } else {
                    return "all"
                }
            },
            noOpClicked);

        packagesAdded++;
    }
    document.getElementById('packages-heading').innerHTML = "<b>Packages</b> - " + packagesAdded + " of " + testDataPackages.length;

    let testDataClasses = testData['classes'];
    var classesAdded = 0;
    for (var i = 0; i < testDataClasses.length; i++) {
        let classData = testDataClasses[i];
        if (selectedAnnotationsSet.size > 0) {
            if (classData.annotationGroups == null || !checkIfAllAnnotationsFound(classData.annotationGroups, selectedAnnotationsSet)) {
                continue;
            } else {
                console.log(classData.annotationGroups)
            }
        }
        addElement("class" + i,
            "class",
            testDataClasses[i],
            classesFieldSet,
            false,
            function (data) {
                return data.path
            },
            noOpClicked);

        classesAdded++;
    }
    document.getElementById('classes-heading').innerHTML = "<b>Classes</b> - " + classesAdded + " of " + testDataClasses.length;
}

function noOpClicked(data, checked) {
    // NO-OP
}

function addElement(id, name, data, fieldContainer, isChecked, labelFunc, clickListener) {
    let divContainer = document.createElement('div')

    let packageInput = document.createElement("input");
    packageInput.placeholder = id;
    packageInput.type = "checkbox";
    packageInput.id = id;
    packageInput.name = name;
    packageInput.checked = isChecked;
    packageInput.value = labelFunc(data);
    packageInput.addEventListener('change', function () {
        console.log(id + " check changed: " + this.checked);
        clickListener(data, this.checked);
    });

    let packageLabel = document.createElement("label");
    packageLabel.textContent = labelFunc(data);
    packageLabel.setAttribute("for", id);

    divContainer.appendChild(packageInput);
    divContainer.appendChild(packageLabel);
    fieldContainer.appendChild(divContainer);
}

function checkIfAllAnnotationsFound(annotationGroups, requiredAnnotations) {
    for (let group of annotationGroups) {
        var match = true;
        for (let requiredAnnotation of requiredAnnotations) {
            if (!group.includes(requiredAnnotation)) {
                match = false;
            }
        }
        if (match) {
            return true;
        }
    }
    return false;
}

load();
