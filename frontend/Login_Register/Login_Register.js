document.addEventListener('DOMContentLoaded', () => {
    //Get the API URL
    const apiUrl = window.API_URL || 'http://localhost:8080';
    const tabButtons = document.querySelectorAll('.tab-button');
    const forms = document.querySelectorAll('.form');
    const toggleButtons = document.querySelectorAll('.toggle-button');
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const errorMessage = document.getElementById('errorMessage');

    // Tab switching
    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            tabButtons.forEach(btn => btn.classList.remove('active'));
            forms.forEach(form => form.classList.remove('active'));
            button.classList.add('active');
            document.querySelector(`.form#${button.dataset.tab}Form`).classList.add('active');
            errorMessage.textContent = '';
        });
    });

    // Toggle buttons
    toggleButtons.forEach(button => {
        button.addEventListener('click', () => {
            button.parentElement.querySelectorAll('.toggle-button').forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');
        });
    });


   // Form submission
    [loginForm, registerForm].forEach(form => {
        form.addEventListener('submit', async (e) => {
            e.preventDefault(); // Prevent default form submission behavior
            const formData = new FormData(form);
            const userType = form.querySelector('.toggle-button.active')?.dataset.value;

            if (userType === 'student') {
                try {
                    const url = form.id === 'registerForm'
                        ? `${apiUrl}/api/students/register`
                        : `${apiUrl}/api/students/login`;

                    console.log('API URL:', url);

                    const body = form.id === 'registerForm' // For register
                        ? JSON.stringify({
                            username: formData.get('username'),
                            email: formData.get('email'),
                            password: formData.get('password'),
                            role: 'student'
                        }) 
                        : new URLSearchParams({ // For login
                            email: formData.get('email'),
                            password: formData.get('password')
                        }).toString();

                    const headers = form.id === 'registerForm'
                        ? { 'Content-Type': 'application/json' }
                        : { 'Content-Type': 'application/x-www-form-urlencoded' };



                    // Make the API call
                    const response = await fetch(url, {
                        method: 'POST',
                        headers: headers,
                        body: body
                    });

                    if (!response.ok) {
                        const errorMsg = await response.text();
                        throw new Error(errorMsg || 'Error occurred');
                    }

                    // Handle the response
                    if (form.id === 'loginForm') {
                        const user = await response.json();
                        console.log('Logged in as:', user.username);

                        // Save user in localStorage to transfer his information to the next page
                        localStorage.clear();
                        localStorage.setItem('user', JSON.stringify(user));

                        alert('Login successful');
                    } else {
                        const user = await response.json();
                        console.log('Logged in as:', user.username);

                        // Save user in localStorage to transfer his information to the next page
                        localStorage.clear();
                        localStorage.setItem('user', JSON.stringify(user));
                        alert('Registration successful');
                    }

                    //Redirect to the next page
                   window.location.href = "../Student_View/StudentView.html";
                    errorMessage.textContent = '';

                } catch (error) {
                    console.error('Submission error:', error);
                    errorMessage.textContent = error.message || 'An error occurred. Please try again.';
                }
            } else { //here should be the instructor code
                errorMessage.textContent = 'Please select "I\'m a student" before submitting.';
            }
        });
    });

});
