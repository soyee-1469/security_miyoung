
$(document).ready(() => {
    checkToken();
    setupAjax();

    getUserInfo().then((userInfo) => {

        console.log('userinfo :: ', userInfo);
        $('#welcome-message').text(userInfo.userName + '(' + userInfo.userId + ')님 환영합니다!');
        $('#hiddenUserId').val(userInfo.userId);
        $('#hiddenUserName').val(userInfo.userName);

    }).catch((error) => {
       console.log('Error while fetching user info : ', error);
    });

    getBoards();
});


let getBoards = () => {
    let currentPage = 1;
    const pageSize = 10; // 한 페이지에 보여줄 게시글 수

    // 초기 게시글 로드
    loadBoard(currentPage, pageSize);

    // 다음 페이지 버튼 클릭 이벤트
    $('#nextPage').on('click', () => {
        currentPage++;
        loadBoard(currentPage, pageSize);
    });

    // 이전 페이지 버튼 클릭 이벤트
    $('#prevPage').on('click', function() {
        if (currentPage > 1) {
            currentPage--;
            loadBoard(currentPage, pageSize);
        }
    });
}

// 게시글 데이터를 로드하는 함수
let loadBoard = (page, size) => {
    $.ajax({
        type: 'GET',
        url: '/api/board',
        data: {
            page: page,
            size: size
        },
        success: (response) => {
            $('#boardContent').empty(); // 기존 게시글 내용 비우기
            if (response.boards.length <= 0) {
                // 게시글이 없는 경우 메시지 출력
                $('#boardContent').append(
                    `<tr>
                        <td colspan="4" style="text-align: center;">글이 존재하지 않습니다.</td>
                    </tr>`
                );
            } else {
                response.boards.forEach((item) => {
                    $('#boardContent').append(
                        `
                    <tr>
                        <td>${item.id}</td>
                        <td><a href="/detail?id=${item.id}">${item.title}</a></td>
                        <td>${item.userId}</td>
                        <td>${item.created}</td>
                    </tr>
                    `
                    );
                });

            }
            // 페이지 정보 업데이트
            $('#pageInfo').text(page);

            // 이전/다음 버튼 상태 설정
            $('#prevPage').prop('disabled', page === 1);
            $('#nextPage').prop('disabled', response.last);
        },
        error: function (xhr, error) {
            if (xhr.status === 401) {
                handleTokenExpiration();
            } else {
                console.error('오류 발생:', error);
                alert('게시판 데이터를 불러오는데 오류가 발생했습니다.');
            }
        }
    });
}

let logout = () => {
    $.ajax({
        type: 'POST',
        url: '/logout',
        contentType: 'application/json; charset=utf-8',
        success:(response) => {
            alert(response.message);
            localStorage.removeItem('accessToken');
            window.location.href = response.url;
        },
        error: (error) => {
            console.log('logout 오류 발생 :: ', error);
            alert('로그아웃 중 오류가 발생했습니다!');
        }

    });
}