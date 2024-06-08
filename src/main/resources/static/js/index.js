function exportState() {
    fetch('/export')
        .then(response => response.json())
        .then(data => {
            const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'game_state.json';
            a.click();
            URL.revokeObjectURL(url);
        });
}

function importState(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const content = e.target.result;
            fetch('/import', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: content
            })
                .then(response => response.text())
                .then(message => {
                    alert(message);
                    location.reload();
                });
        };
        reader.readAsText(file);
    }
}