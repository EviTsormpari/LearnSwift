'use strict';
        let reviews = [];
        let currentInstructor = '';
        let currentRating = 0;
        let currentCourse = '';
        let currentInstructorName = '';

        const apiUrl = window.API_URL || 'http://localhost:8080';  // Παράμετρος αν δεν είναι διαθέσιμη η μεταβλητή


        async function searchCourses() {
            const searchTerm = document.getElementById('courseSearchInput').value.toLowerCase();
        
            // Fetch all courses from the backend
            fetch(`${apiUrl}/api/courses`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(courses => {
                    // Filter the courses based on the search term
                    const results = courses.filter(course => 
                        course.title.toLowerCase().includes(searchTerm)
                    );
                    displayCourseTitle(results);
                })
                .catch(error => {
                    console.error('Error fetching courses:', error);
                    // Optionally, you can show an error message to the user
                });
        }
        
        // Displaying the results (only title) with a button to view the details of the course
        async function displayCourseTitle(results) {
            const searchResults = document.getElementById('searchResults');
            searchResults.innerHTML = results.length > 0 
                ? results.map(item => `
                    <div class="result-item">
                        <span>${item.title}</span>
                        <div>
                            <button class="view-details" onclick="viewCourseDetails('${item.title}')">View Details</button>
                        </div>
                    </div>
                `).join('')
                : '<p>No results found</p>';
            hideAllSections();
            searchResults.classList.remove('hidden');
        }


        async function searchInstructors() {
            const searchTerm = document.getElementById('instructorSearchInput').value.toLowerCase();
            
            // Fetch all instructors from the backend
            fetch(`${apiUrl}/api/instructors`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(instructors => {
                    // Filter the instructors based on the search term
                    const results = instructors.filter(instructor => 
                        instructor.username.toLowerCase().includes(searchTerm)
                    );
                    displayInstructorResults(results);
                })
                .catch(error => {
                    console.error('Error fetching instructors:', error);
                });
        }

        //displaying the instructor results with a 
        async function displayInstructorResults(results) {
            const searchResults = document.getElementById('searchResults');
            searchResults.innerHTML = results.length > 0 
                ? results.map(instructor => `
                    <div class="instructor-item">
                        <h3>${instructor.username}</h3>
                        <div class="instructor-courses" id="courses-${instructor.username}"></div>
                    </div>
                `).join('')
                : '<p>No results found</p>';
    
            hideAllSections();
            searchResults.classList.remove('hidden');

            // Fetch courses for each instructor
            results.forEach(instructor => {
                fetch(`${apiUrl}/api/instructors/${instructor.username}/courses`)
                    .then(response => response.json())
                    .then(courses => {
                        // Populate the courses for each instructor
                        getCoursesWithDetails(courses, instructor);
                    })
                    .catch(error => console.error('Error fetching courses for instructor:', error));
            });
}

        async function getCoursesWithDetails(courses, instructor) {
            const coursesHtmlPromises = courses.map(course => {
                // Fetch the price and duration for each course
                return Promise.all([
                    fetch(`${apiUrl}/api/instructors/${instructor.username}/courses/${course.course_id}/course_price`)
                        .then(response => response.json()), // Price
                    fetch(`${apiUrl}/api/instructors/${instructor.username}/courses/${course.course_id}/course_duration`)
                        .then(response => response.json()) // Duration
                ]).then(([price, duration]) => {
                    return `
                        <div class="course-item">
                            <span>${course.title} - Duration: ${duration} minutes - Price: $${price}</span>
                            <button 
                                class="book-course" 
                                data-course-title="${course.title}" 
                                data-course-id="${course.course_id}" 
                                data-course-price="${price}" 
                                data-course-duration="${duration}" 
                                data-instructor-id="${instructor.user_id}" 
                                data-instructor-name="${instructor.username}" 
                                data-instructor-email="${instructor.email}"
                                data-instructor-password="${instructor.password}"
                                onclick="showBookingPage(this)">
                                Book Course
                            </button>


                        </div>
                 `;
                });
            });

            // Once all course details are fetched, update the instructor's courses section
            Promise.all(coursesHtmlPromises).then(courseItems => {
                document.getElementById(`courses-${instructor.username}`).innerHTML = courseItems.join('');
            });
        }


        async function showBookingPage(button){
            // Parse data attributes back into objects
            const courseTitle = button.dataset.courseTitle;
            const courseId = parseInt(button.dataset.courseId,10);
            const coursePrice = parseFloat(button.dataset.coursePrice);
            const courseDuration = parseInt(button.dataset.courseDuration,10);
            const instructorId = parseInt(button.dataset.instructorId,10);
            const instructorName = button.dataset.instructorName;
            const instructorEmail = button.dataset.instructorEmail;
            const instructorPassword = button.dataset.instructorPassword;
        
            // Construct the course object
            const course = {
                course_id: courseId,
                title: courseTitle,
                instructors: [
                    {
                        user_id: instructorId
                    }
                ]
            };

            const courses_price = {};
            const courses_duration = {};

            // Explicitly assign numeric keys
            courses_price[button.dataset.courseId] = coursePrice;
            courses_duration[button.dataset.courseId] = courseDuration;
        
            // Construct the instructor object
            const instructor = {
                user_id: instructorId,
                username: instructorName,
                email: instructorEmail,
                password: instructorPassword,
                role: "instructor",
                courses_price: courses_price, // Assign the object explicitly
                courses_duration: courses_duration, // Assign the object explicitly
                reviews: []
            };

            currentCourse = course;
            currentInstructor = instructor

            document.getElementById('bookingPage').classList.remove('hidden');
            document.getElementById('overlay').classList.remove('hidden');
            populateTimeOptions();
        }

        async function closeBookingPage() {
            document.getElementById('bookingPage').classList.add('hidden');
            document.getElementById('overlay').classList.add('hidden');
        }

        async function populateTimeOptions() {
            const startTime = document.getElementById('startTime');
            const endTime = document.getElementById('endTime');
            startTime.innerHTML = '';
            endTime.innerHTML = '';

            for (let i = 8; i <= 20; i++) {
                for (let j = 0; j < 60; j += 30) {
                    const time = `${i.toString().padStart(2, '0')}:${j.toString().padStart(2, '0')}`;
                    startTime.options.add(new Option(time, time));
                    endTime.options.add(new Option(time, time));
                }
            }
        }

        document.getElementById('bookingForm').addEventListener('submit', function(e) {
            e.preventDefault();
            confirmBooking();
        });



        async function showReviewForm(instructor) { //this function isnt available
            currentInstructor = instructor;
            document.getElementById('reviewForm').classList.remove('hidden');
        }

        async function setRating(rating) { //this function isnt available
            currentRating = rating;
            const stars = document.querySelectorAll('.star-rating > span');
            stars.forEach((star, index) => {
                star.textContent = index < rating ? '★' : '☆';
            });
        }

        async function submitReview() { //this function isnt available
            const reviewText = document.getElementById('reviewText').value;
            if (currentRating && reviewText) {
                const review = { instructor: currentInstructor, rating: currentRating, reviewText };
                reviews.push(review);
                alert('Review submitted successfully!');
                document.getElementById('reviewForm').classList.add('hidden');
                document.getElementById('reviewText').value = '';
                setRating(0);
            } else {
                alert('Please provide both a rating and a review');
            }
        }

        async function confirmBooking() {
            const date = document.getElementById('bookingDate').value;
            const startTime = document.getElementById('startTime').value;
            const endTime = document.getElementById('endTime').value;

            const user = JSON.parse(localStorage.getItem('user'));

            // Check if the user is already logged in
            if (!user) {
                console.error('No student found in localStorage');
                userProfile.innerHTML = `<p>No student data found. Please log in again.</p>`;
                userProfile.classList.remove('hidden');
                return;
            }

            //fetch student from backend to take all the information
            const response = await fetch(`${apiUrl}/api/students/${user.username}`);
            if (!response.ok) {
                throw new Error(`Failed to fetch user data. Status: ${response.status}`);
            }

            const currentStudent = await response.json();

            if (date && startTime && endTime) {
                const booking = { 
                    course: currentCourse, // The complete course object
                    student: currentStudent, // The complete Student object
                    instructor: currentInstructor, // The complete Instructor object
                    bookingDate: date, // LocalDate to string format
                    booking_status: 'Pending', // Set booking status
                    startTime: startTime, // LocalTime to string
                    endTime: endTime // LocalTime to string
                };
        
                try {
                    //API call to create a new booking
                    const response1 = await fetch(`${apiUrl}/api/bookings`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(booking)
                    });

                    console.log("BODY",JSON.stringify(booking));
            
                    if (response1.ok) {
                        const successMessage = await response1.text();  // Get the response as text
                        console.log("Booking Success:", successMessage);
                        alert(successMessage);
                        //updateBookings();
                        closeBookingPage();
                    } else {
                        const errorData = await response1.text();
                        alert(errorData);
                    }
                } catch (error) {
                    console.error('Error creating booking:', error);
                    alert('Failed to create booking. Please try again later.');
                }
            } else {
                alert('Please fill in all booking details.');
            }
        }
        

        async function showBookings() {
            hideAllSections();
            const bookingsDiv = document.getElementById('bookings');
            bookingsDiv.classList.remove('hidden');
            updateBookings();
        }

        async function updateBookings() {
            const bookingsDiv = document.getElementById('bookings');
            bookingsDiv.innerHTML = bookings.length > 0
                ? bookings.map((booking, index) => `
                    <div class="booking-item">
                        <span>Course: ${booking.course}, Instructor: ${booking.instructor}, Date: ${booking.date}, Time: ${booking.startTime} - ${booking.endTime}</span>
                        <button class="delete-booking" onclick="deleteBooking(${booking.booking_id})">Delete</button>
                    </div>
                `).join('')
                : '<p>No bookings  found</p>';
        }

        async function showBookings(studentId) {
            hideAllSections();
            const bookingsDiv = document.getElementById('bookings');
            bookingsDiv.classList.remove('hidden');
        
            const user = JSON.parse(localStorage.getItem('user'));
        
            //Check if user has already logged in
            if (!user) {
                console.error('No student found in localStorage');
                userProfile.innerHTML = `<p>No student data found. Please log in again.</p>`;
                userProfile.classList.remove('hidden');
                return; 
            }
        
            // Fetch student from backend fot all the information
            const response = await fetch(`${apiUrl}/api/students/${user.username}`);
            if (!response.ok) {
                throw new Error(`Failed to fetch user data. Status: ${response.status}`);
            }
        
            const student = await response.json();
        
            try {
                // Fetch initial list of bookings (only containing booking IDs)
                const response1 = await fetch(`${apiUrl}/api/students/${student.username}/bookings`);
                if (!response1.ok) {
                    throw new Error(`Failed to fetch bookings. Status: ${response1.status}`);
                }
        
                const bookingIds = await response1.json();
                console.log('Booking IDs:', bookingIds);
        
                // Fetch each booking by its ID to take all the information
                const bookings = await Promise.all(
                    bookingIds.map(async (booking) => {
                        const response2 = await fetch(`${apiUrl}/api/bookings/${booking.booking_id}`);
                        if (!response2.ok) {
                            console.error(`Failed to fetch booking ID ${booking.booking_id}. Status: ${response2.status}`);
                            return null; // Skip this booking if it fails
                        }
                        return await response2.json();
                    })
                );
        
                // Filter out any failed bookings
                const validBookings = bookings.filter(booking => booking !== null);
        
                // Update the bookings section with the retrieved data
                bookingsDiv.innerHTML = validBookings.length > 0
                    ? validBookings.map((booking, index) => `
                        <div class="booking-item">
                            <span>Course: ${booking.course_title}, Instructor: ${booking.instructorUsername}, Date: ${booking.bookingDate}, Time: ${booking.startTime} - ${booking.endTime}</span>
                            <button class="delete-booking" onclick="deleteBooking(${booking.booking_id})">Delete</button>
                        </div>
                    `).join('')
                    : '<p>No bookings found</p>';
            } catch (error) {
                console.error('Error fetching bookings:', error);
                bookingsDiv.innerHTML = '<p>Error loading bookings. Please try again later.</p>';
            }
        }
        

        async function deleteBooking(booking_id) {
            console.log("Booking ID received in deleteBooking:", booking_id);
            try {
                //API call to delete bookings
                console.log(`Attempting to DELETE at URL: http://localhost:8080/api/bookings/${booking_id}`);
                const response = await fetch(`${apiUrl}/api/bookings/${booking_id}`, {
                    method: 'DELETE',
                });
        
                // Check the response status
                if (response.ok) {
                    const message = await response.text();
                    console.log(message);
                    alert("Booking deleted successfully");
                } else if (response.status === 404) {
                    alert("Booking not found");
                } else {
                    const errorMessage = await response.text();
                    console.error('Error deleting booking:', errorMessage);
                    alert(`Error deleting booking: ${errorMessage}`);
                }
            } catch (error) {
                console.error('Unexpected error during booking deletion:', error);
                alert("Unexpected error. Please try again later.");
            }
            updateBookings();
        }

        async function viewCourseDetails(courseName) {
            hideAllSections();
            const courseDetails = document.getElementById('courseDetails');
        
            // Fetch instructors for the specified course
            fetchInstructors(courseName)
                .then(instructors => {
                    if (!instructors || instructors.length === 0) {
                        throw new Error(`No instructors found for course: ${courseName}`);
                    }
                    return Promise.all(instructors.map(instructor => 
                        fetchInstructorCourseDetails(instructor, instructor.courses) // fetch details about the instructor's courses
                    ));
                })
                .then(instructorsDetails => {
                    displayCourseDetails(courseName, instructorsDetails); //display results
                })
                .catch(error => {
                    console.error('Error fetching course details:', error);
                    courseDetails.innerHTML = `<p>Error loading course details. Please try again later.</p>`;
                    courseDetails.classList.remove('hidden');
                });
        }
        
        async function fetchInstructors(courseName) {
            //API request for instructors who teach a specific course
            return fetch(`${apiUrl}/api/courses/${courseName}/instructors`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(instructors => {
                    return instructors;
                });
        }
        
        async function fetchInstructorCourseDetails(instructor, courses) {
            const coursePromises = courses.map(course => {
                const courseId = course.course_id;
                const courseTitle = course.title;
                return Promise.all([
                    fetchCoursePrice(instructor.username, courseId),
                    fetchCourseDuration(instructor.username, courseId)
                ]).then(([price, duration]) => ({
                    courseId,
                    courseTitle,
                    price,
                    duration
                }));
            });
        
            const coursesDetails = await Promise.all(coursePromises);
            
            return {
                instructorUsername: instructor.username,
                instructorId :instructor.user_id,
                instructorPassword: instructor.password,
                instructorEmail: instructor.email,
                coursesDetails
            };
        }
        
        async function fetchCoursePrice(username, courseId) {
            //fetch the price for the course
            return fetch(`${apiUrl}/api/instructors/${username}/courses/${courseId}/course_price`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Error fetching price');
                    }
                    return response.json(); 
                });
        }
        
        async function fetchCourseDuration(username, courseId) {
            //fetch the duration of the course
            return fetch(`${apiUrl}/api/instructors/${username}/courses/${courseId}/course_duration`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Error fetching duration');
                    }
                    return response.json();
                });
        }
        
        async function displayCourseDetails(courseName, instructorsDetails) {
            const courseDetails = document.getElementById('courseDetails');
            
            const instructorsList = instructorsDetails.map(instructorDetail => {
                // Filter the courses to include only the ones that match the specified courseName
                const filteredCourses = instructorDetail.coursesDetails.filter(courseDetail => courseDetail.courseTitle === courseName);
                
                if (filteredCourses.length === 0) {
                    return ''; // Skip instructors with no matching courses
                }
        
                const coursesList = filteredCourses.map(courseDetail => `
                    <li class="instructor-list-item">
                        <div>
                            <strong>${instructorDetail.instructorUsername}</strong><br>
                            Course Title: ${courseDetail.courseTitle}<br>
                            Duration: ${courseDetail.duration} minutes<br>
                            Price: $${courseDetail.price}
                        </div>
                         <button 
                                class="book-course" 
                                data-course-title="${courseDetail.courseTitle}" 
                                data-course-id="${courseDetail.courseId}" 
                                data-course-price="${courseDetail.price}" 
                                data-course-duration="${courseDetail.duration}" 
                                data-instructor-id="${instructorDetail.instructorId}" 
                                data-instructor-name="${instructorDetail.instructorUsername}" 
                                data-instructor-email="${instructorDetail.instructorEmail}"
                                data-instructor-password="${instructorDetail.instructorPassword}"
                                onclick="showBookingPage(this)">
                                Book Course
                            </button>
                    </li>
                `).join('');
        
                return `<ul>${coursesList}</ul>`;
            }).join('');
        
            courseDetails.innerHTML = `
                <h2>${courseName} Details</h2>
                <p><strong>Instructors:</strong></p>
                ${instructorsList}
            `;
            courseDetails.classList.remove('hidden');
        }
        

        async function showUserProfile(studentUsername) {
            hideAllSections();
            const userProfile = document.getElementById('userProfile');
            
            const user = JSON.parse(localStorage.getItem('user'));

            // Check if the user is already logged in
            if (!user) {
                console.error('No student found in localStorage');
                userProfile.innerHTML = `<p>No student data found. Please log in again.</p>`;
                userProfile.classList.remove('hidden');
                return;
            }
        
            try {
                //fetch the student with all the information
                const response = await fetch(`${apiUrl}/api/students/${user.username}`);
                if (!response.ok) {
                    throw new Error(`Failed to fetch user data. Status: ${response.status}`);
                }
        
                const student = await response.json();
        
                userProfile.innerHTML = `
                    <h2>User Profile</h2>
                    <div class="user-info">
                        <label>Username:</label>
                        <span id="usernameDisplay">${student.username}</span>
                        <button onclick="editUsername()">Edit</button>
                    </div>
                    <div class="user-info">
                        <label>Email:</label>
                        <span>${student.email}</span>
                    </div>
                    <div class="user-info">
                        <label>Password:</label>
                        <span>********</span>
                        <button onclick="editPassword()">Change Password</button>
                    </div>
                    <div class="user-info">
                        <label>Role:</label>
                        <span>${student.role}</span>
                    </div>
                    <div class="user-info">
                        <button onclick="logout()" style="background-color: red; color: white; border: none; padding: 10px 20px; cursor: pointer;">Log Out</button>
                    </div>
                `;
                userProfile.classList.remove('hidden');
            } catch (error) {
                console.error('Error fetching user data:', error);
                userProfile.innerHTML = `<p>Error loading user profile. Please try again later.</p>`;
                userProfile.classList.remove('hidden');
            }
        }
        

        async function editUsername() {
            const user = JSON.parse(localStorage.getItem('user'));

            // Check if the user is already logged in
            if (!user) {
                console.error('No student found in localStorage');
                userProfile.innerHTML = `<p>No student data found. Please log in again.</p>`;
                userProfile.classList.remove('hidden');
                return;
            }


            const newUsername = prompt("Enter new username:", user.username);
                try {
                    //fetch the student with all the information
                    const response = await fetch(`${apiUrl}/api/students/${user.username}`);
                    if (!response.ok) {
                        throw new Error(`Failed to fetch user data. Status: ${response.status}`);
                    }
        
                    const student = await response.json();

                    const body = new URLSearchParams({
                        username: newUsername
                    }).toString();

                    //API call to edit student's username
                    const response1 = await fetch(`${apiUrl}/api/students/${student.user_id}/username/${newUsername}`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: body
                    });

                    if (response1.ok) {
                        localStorage.clear();
                        student.username = newUsername;
                        document.getElementById('usernameDisplay').textContent = newUsername;
                        localStorage.setItem('user', JSON.stringify(student));
                        alert("Username updated successfully!");
                    } else {
                        const errorMessage = await response.text();
                        alert(`Error: ${errorMessage}`);
                    }
                } catch (error) {
                    console.error("Error updating username:", error);
                    alert("An unexpected error occurred. Please try again.");
                }
        }
        
        async function editPassword() {
            const user = JSON.parse(localStorage.getItem('user'));

            // Check if user already logged in
            if (!user) {
                console.error('No student found in localStorage');
                userProfile.innerHTML = `<p>No student data found. Please log in again.</p>`;
                userProfile.classList.remove('hidden');
                return;
            }


            const currentPassword = prompt("Enter current password:");
            if (currentPassword === user.password) {
                const newPassword = prompt("Enter new password:");
                    try {
                        //fetch student from backend for all the information
                        const response = await fetch(`${apiUrl}/api/students/${user.username}`);
                        if (!response.ok) {
                            throw new Error(`Failed to fetch user data. Status: ${response.status}`);
                        }
        
                        const student = await response.json();

                        const body = new URLSearchParams({
                            password: newPassword
                        }).toString();

                        //API call to edit student's password
                        const response1 = await fetch(`${apiUrl}/api/students/${student.user_id}/password/${newPassword}`, {
                            method: 'PUT',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded',
                            },
                            body: body
                        });

                        console.log("user_id:", student.user_id);
        
                        if (response1.ok) {
                            //clear local storage
                            localStorage.clear();
                            student.password = newPassword;
                            localStorage.setItem('user', JSON.stringify(student));

                             // Redirect to login screen
                            window.location.href = '../Login_Register/Login_Register.html';
                            alert("Password changed successfully! Please log in again.");
                        } else {
                            const errorMessage = await response.text();
                            console.error("Error updating password:", errorMessage);
                            alert(`Error: ${errorMessage}`);
                        }
                    } catch (error) {
                        console.error("Error updating password:", error);
                        alert("An unexpected error occurred. Please try again.");
                    }
                //}
            } else {
                alert("Incorrect current password. Password not changed.");
            }
        }
        
        function logout() {
            // Delete student from local storage
            localStorage.removeItem('user');
    
            // Redirect to login page
            window.location.href = '../Login_Register/Login_Register.html';
            alert("You have been logged out successfully.");
        }

        function hideAllSections() {
            const sections = ['searchResults', 'bookings', 'instructorInfo', 'courseDetails', 'userProfile', 'reviewForm'];
            sections.forEach(section => document.getElementById(section).classList.add('hidden'));
        }