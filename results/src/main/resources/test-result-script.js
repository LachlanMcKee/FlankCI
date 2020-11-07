function openDialog(dialogIndex) {
    var dialog = document.getElementById('dialog' + dialogIndex);

    dialog.showModal();

    dialog.querySelector('.close').addEventListener('click', function() {
        dialog.close();
    });
}
