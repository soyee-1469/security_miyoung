// Wait until the DOM is fully loaded
$(document).ready(function () {
    Kakao.init('8fbd1a3d9873c9b86405417b7d1d10c8'); // Initialize with your Kakao JavaScript Key

    // Event listener for the Kakao login button
    $('#kakao-login-btn').on('click', function () {
        Kakao.Auth.login({
            success: function (authObj) {
                // Call function to handle login success
                handleKakaoLogin(authObj.access_token);
            },
            fail: function (err) {
                alert("Kakao Login Failed: " + JSON.stringify(err));
            }
        });
    });
});

// Function to handle successful Kakao login
function handleKakaoLogin(accessToken) {
    if (!accessToken) {
        alert("Access token not available.");
        return;
    }

    // 백엔드에 Kakao Access Token 전달
    fetch('/api/kakao/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ kakaoAccessToken: accessToken }),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed with status: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            if (data.token) {
                // JWT 토큰 저장
                localStorage.setItem('accessToken', data.token);

                // 리다이렉트 수행
                if (data.redirectUrl) {
                    console.log("Redirecting to:", data.redirectUrl);
                    window.location.href = data.redirectUrl;
                } else {
                    alert("Redirect URL not received from server.");
                }
            } else {
                alert("JWT token not received.");
            }
        })
        .catch(error => {
            console.error("Error during login:", error);
            alert("Login failed: " + error.message);
        });
}