let setupAjax = () => {
    $.ajaxSetup({
        beforeSend: (xhr) => {
            let token = localStorage.getItem("accessToken");
            if (token) {
                xhr.setRequestHeader('Authorization', 'Bearer ' + token);
            }
        }
    });
}

let handleTokenExpiration = () => {
    $.ajax({
        type: 'POST',
        url: '/refresh-token',
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        xhrFields: {
            withCredentials: true // 쿠키를 포함해서 요청을 보냄
        },
        success: (response) => {
            console.log('response :: ', response);
            localStorage.setItem('accessToken', response.accessToken);
        },
        error: () => {
            alert('로그인이 필요합니다. 다시 로그인해주세요.');
            localStorage.removeItem('accessToken');
            window.location.href = '/member/login';
        }
    });
}


let checkToken = () => {
    let token = localStorage.getItem("accessToken");
    if (token == null || token.trim() === '') {
        window.location.href = '/member/login';
    }
}

let getUserInfo = () => {
    return new Promise( (resolve, reject) => {
        $.ajax({
            type: 'GET',
            url: '/user/info',
            success: (response) => {
                resolve(response);
            },
            error: (xhr) => {
                if (xhr.status === 401) {
                    handleTokenExpiration();
                } else {
                    reject(xhr); // 오류가 발생한 경우 Promise를 거부
                }
            }
        });
    });
}
