import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-novi-centar',
  templateUrl: './novi-centar.component.html',
  styleUrls: ['./novi-centar.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class NoviCentarComponent {
  centarForm: FormGroup;
  submitted = false;
  successMessage = '';
  errorMessage = '';
  
  // File upload fields
  selectedImage: File | null = null;
  selectedPdf: File | null = null;

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.centarForm = this.fb.group({
      ime: ['', Validators.required],
      ophis: ['', Validators.required],
      adresa: ['', Validators.required],
      grad: ['', Validators.required],
      discipline: ['', Validators.required]
    });
  }

  get ime() { return this.centarForm.get('ime'); }
  get ophis() { return this.centarForm.get('ophis'); }
  get adresa() { return this.centarForm.get('adresa'); }
  get grad() { return this.centarForm.get('grad'); }
  get discipline() { return this.centarForm.get('discipline'); }

  onImageSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedImage = file;
    }
  }

  onPdfSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedPdf = file;
    }
  }

  onSubmit() {
    this.submitted = true;

    if (this.centarForm.invalid) {
      return;
    }

    // Create FormData for multipart/form-data request
    const formData = new FormData();
    formData.append('ime', this.centarForm.value.ime);
    formData.append('ophis', this.centarForm.value.ophis);
    formData.append('adresa', this.centarForm.value.adresa);
    formData.append('grad', this.centarForm.value.grad);
    
    // Add disciplines as separate entries
    const disciplines = this.centarForm.value.discipline.split(',').map((d: string) => d.trim());
    disciplines.forEach((discipline: string) => {
      formData.append('discipline', discipline);
    });

    // Add files if selected
    if (this.selectedImage) {
      formData.append('image', this.selectedImage);
    }
    if (this.selectedPdf) {
      formData.append('pdf', this.selectedPdf);
    }

    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': token || ''
      // Don't set Content-Type - let browser set it with boundary for multipart/form-data
    });

    this.http.post<any>('http://localhost:8080/api/centri/novicentar', formData, { headers })
      .subscribe({
        next: (response: any) => {
          this.successMessage = 'Centar successfully created with all files!';
          this.errorMessage = '';
          this.resetForm();
        },
        error: (err: any) => {
          this.errorMessage = `Failed to create centar: ${err.message}`;
          this.successMessage = '';
        }
      });
  }

  resetForm() {
    this.centarForm.reset();
    this.submitted = false;
    this.selectedImage = null;
    this.selectedPdf = null;
    
    // Reset file inputs
    const imageInput = document.getElementById('imageInput') as HTMLInputElement;
    const pdfInput = document.getElementById('pdfInput') as HTMLInputElement;
    if (imageInput) imageInput.value = '';
    if (pdfInput) pdfInput.value = '';
  }
}
