let selectedFile = null; // 파일은 1개만 선택 가능

$(document).ready(() => {
    checkToken();
    setupAjax();

    getUserInfo().then((userInfo) => {
        $('#hiddenUserId').val(userInfo.userId);
        $('#hiddenUserName').val(userInfo.userName);
        $('#userId').val(userInfo.userName + '(' +userInfo.userId + ')');
    }).catch((error) => {
        console.log('Error while fetching user info : ', error);
    })

    saved();
    fileChaged();
});

let saved = () => {
    $('#submitBtn').on('click', (event) => {
        event.preventDefault();

        let formData = new FormData($('#writeForm')[0]);

        $.ajax({
            type: 'POST',
            url: '/api/board', // 서버의 엔드포인트 URL
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                // 성공 시 실행될 콜백 함수
                alert('게시글이 성공적으로 등록되었습니다!')
                // 성공 후 다른 페이지로 이동하거나 처리할 코드 작성 가능
                window.location.href = '/';
            },
            error: function(error) {
                // 실패 시 실행될 콜백 함수
                console.error('오류 발생:', error);
                alert('게시글 등록 중 오류가 발생하였습니다.');
            }
        });

    });
}

let fileChaged = () => {
    // 파일 선택 시 이벤트
    $('#file').on('change', function(e) {
        const file = e.target.files[0]; // 첫 번째 파일만 선택

        selectedFile = file; // 선택된 파일을 변수에 저장
        updateFileList(); // 파일 목록 업데이트
    });
}

// 파일 목록 업데이트 함수 (파일 하나만)
let updateFileList = () => {
    $('#fileList').empty(); // 기존 목록 비우기

    if (selectedFile) {
        $('#fileList').append(`
                    <li>
                        ${selectedFile.name} <button type="button" class="remove-btn">X</button>
                    </li>
                `);

        // X 버튼 클릭 시 파일 제거
        $('.remove-btn').on('click', function () {
            selectedFile = null; // 선택된 파일 제거
            $('#file').val(''); // 파일 input 초기화
            updateFileList(); // 파일 목록 갱신
        });
    }
}