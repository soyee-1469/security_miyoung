$(document).ready(() => {
    $('#signin').click(() => {
        let userId = $('#user_id').val();
        let password = $('#password').val();

        let formData = { userId, password };

        $.ajax({
            type: 'POST',
            url: '/login',
            data: JSON.stringify(formData),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: (response) => {
                console.log('res :: ', response);
                if (response.loggedIn) {
                    alert(response.message);
                    localStorage.setItem('accessToken', response.accessToken);
                    window.location.href = response.url;
                }
            },
            error: (error) => {
                console.error('오류 발생:', error);
            }
        });
    });
});
