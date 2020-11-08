function openDialog(dialogIndex) {
    var dialog = document.getElementById('dialog' + dialogIndex);

    dialog.showModal();
    dialog.addEventListener('click', outsideDialog);

    dialog.querySelector('.close').addEventListener('click', function() {
        dialog.close();
    });
}

// https://github.com/google/material-design-lite/issues/5030#issuecomment-355489052
function outsideDialog(event) {
    var dialog = this;
    var rect = dialog.getBoundingClientRect();
    var isInDialog = (rect.top <= event.clientY && event.clientY <= rect.top + rect.height
        && rect.left <= event.clientX && event.clientX <= rect.left + rect.width);
    if (!isInDialog) {
        dialog.close();
        dialog.removeEventListener('click', outsideDialog);
    }
}
