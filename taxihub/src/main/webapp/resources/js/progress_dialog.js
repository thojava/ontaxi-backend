var t;

function startTimer() {
    t = setTimeout("PF('statusDialog').show()", 1000);
}

function stopTimer() {
    clearTimeout(t);
}