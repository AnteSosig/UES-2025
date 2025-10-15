import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class EditProfileComponent implements OnInit {
  editForm: FormGroup;
  passwordForm: FormGroup;  // New form for password update
  userInfo: any = null;
  errorMessage: string = '';
  successMessage: string = '';
  passwordSuccessMessage: string = '';

  constructor(
    private http: HttpClient,
    private fb: FormBuilder,
    private router: Router
  ) {
    // Edit profile form
    this.editForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      brojTelefona: ['', Validators.required],
      adresa: ['', Validators.required]
    });

    // New password form
    this.passwordForm = this.fb.group({
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]]
    }, { validator: this.passwordMatchValidator });  // Add custom validator here
  }

  ngOnInit(): void {
    this.fetchUserInfo();
  }

  // Custom validator to check if passwords match
  passwordMatchValidator(group: AbstractControl): { [key: string]: boolean } | null {
    const password = group.get('password')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { 'mismatch': true };
  }

  fetchUserInfo() {
    const token = localStorage.getItem('token');

    if (token) {
      const headers = new HttpHeaders().set('authorization', token);

      this.http.get('http://localhost:8080/api/korisnici/ja', { headers })
        .subscribe(
          (data) => {
            this.userInfo = data;
            // Patch the form with the user data
            this.editForm.patchValue({
              email: this.userInfo.email,
              firstName: this.userInfo.firstName,
              lastName: this.userInfo.lastName,
              brojTelefona: this.userInfo.brojTelefona,
              adresa: this.userInfo.adresa
            });
          },
          (error) => {
            this.errorMessage = 'Failed to load user information';
            console.error(error);
          }
        );
    } else {
      this.errorMessage = 'Authorization token not found. Please log in.';
    }
  }

  // Submit the form to update user details
  onSubmitEdit() {
    if (this.editForm.invalid) {
      return;
    }

    const token = localStorage.getItem('token');
    if (token) {
      const headers = new HttpHeaders().set('authorization', token);

      const formData = {
        email: this.editForm.value.email,
        firstName: this.editForm.value.firstName,
        lastName: this.editForm.value.lastName,
        numTel: this.editForm.value.brojTelefona,
        address: this.editForm.value.adresa
      };

      // Check if the email has been changed
      const emailChanged = this.userInfo.email !== this.editForm.value.email;

      this.http.get('http://localhost:8080/api/korisnici/izmenikorisnika', {
        headers,
        params: formData
      }).subscribe(
        () => {
          if (emailChanged) {
            // Email was changed, so log out the user and redirect to login
            localStorage.removeItem('token');  // Clear the authentication token
            this.router.navigate(['/login']);  // Redirect to login page
          } else {
            this.successMessage = 'User details updated successfully!';
            this.router.navigate(['/profile']);  // Navigate back to the profile page if email wasn't changed
          }
        },
        (error) => {
          this.errorMessage = 'Failed to update user details';
          console.error(error);
        }
      );
    }
  }

  // Submit the form to update password
  onSubmitPassword() {
    if (this.passwordForm.invalid) {
      return;
    }

    const token = localStorage.getItem('token');
    if (token) {
      const headers = new HttpHeaders().set('authorization', token);

      const formData = {
        password: this.passwordForm.value.password
      };

      this.http.get('http://localhost:8080/api/korisnici/izmenikorisnika', {
        headers,
        params: formData
      }).subscribe(
        () => {
          this.passwordSuccessMessage = 'Password updated successfully!';
          this.passwordForm.reset();  // Reset the password form after success
        },
        (error) => {
          this.errorMessage = 'Failed to update password';
          console.error(error);
        }
      );
    }
  }
}
