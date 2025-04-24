function showForm(form) {
  const loginSection = document.getElementById('login-section');
  const signupSection = document.getElementById('signup-section');
  const loginToggle = document.getElementById('login-toggle');
  const signupToggle = document.getElementById('signup-toggle');

  if (form === 'login') {
    loginSection.classList.add('active');
    signupSection.classList.remove('active');
    loginToggle.classList.add('active');
    signupToggle.classList.remove('active');
  } else {
    signupSection.classList.add('active');
    loginSection.classList.remove('active');
    signupToggle.classList.add('active');
    loginToggle.classList.remove('active');
  }
}