import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient, HttpHeaders, HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-novi-centar',
  templateUrl: './novi-centar.component.html',
  styleUrls: ['./novi-centar.component.css'],
  standalone: true,
  imports: [CommonModule, HttpClientModule, ReactiveFormsModule]
})
export class NoviCentarComponent {
  centarForm: FormGroup;
  submitted = false;
  successMessage = '';
  errorMessage = '';
  
  // File upload fields
  selectedImage: File | null = null;
  selectedPdf: File | null = null;
  createdCentarId: number | null = null;

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

    const formData = {
      id: null,
      ime: this.centarForm.value.ime,
      ophis: this.centarForm.value.ophis,
      adresa: this.centarForm.value.adresa,
      grad: this.centarForm.value.grad,
      datumKreacije: null,
      rating: null,
      radnoVremeDTOList: [],
      discipline: this.centarForm.value.discipline.split(',').map((d: string) => d.trim())
    };

    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': token || '',
      'Content-Type': 'application/json'
    });

    this.http.post<any>('http://localhost:8080/api/centri/novicentar', formData, { headers })
      .subscribe({
        next: (response) => {
          this.successMessage = 'Centar successfully created!';
          this.errorMessage = '';
          this.createdCentarId = response.id;

          // If files are selected, upload them
          if ((this.selectedImage || this.selectedPdf) && this.createdCentarId) {
            this.uploadFiles(this.createdCentarId);
          } else {
            // Reset form if no files to upload
            this.centarForm.reset();
            this.submitted = false;
          }
        },
        error: (err) => {
          this.errorMessage = `Failed to create centar: ${err.message}`;
          this.successMessage = '';
        }
      });
  }

  uploadFiles(centarId: number) {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('authorization', token || '');
    const uploadFormData = new FormData();

    if (this.selectedImage) {
      uploadFormData.append('image', this.selectedImage);
    }
    if (this.selectedPdf) {
      uploadFormData.append('pdf', this.selectedPdf);
    }

    this.http.post(`http://localhost:8080/api/centri/${centarId}/upload`, uploadFormData, { headers })
      .subscribe({
        next: () => {
          this.successMessage += ' Files uploaded successfully!';
          this.resetForm();
        },
        error: (err) => {
          this.errorMessage = `Centar created but file upload failed: ${err.message}`;
        }
      });
  }

  resetForm() {
    this.centarForm.reset();
    this.submitted = false;
    this.selectedImage = null;
    this.selectedPdf = null;
    this.createdCentarId = null;
    
    // Reset file inputs
    const imageInput = document.getElementById('imageInput') as HTMLInputElement;
    const pdfInput = document.getElementById('pdfInput') as HTMLInputElement;
    if (imageInput) imageInput.value = '';
    if (pdfInput) pdfInput.value = '';
  }
}
