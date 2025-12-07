import { Component, Input, Output, EventEmitter } from "@angular/core";
import { FormGroup, FormControl } from "@angular/forms";
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from "@angular/material/card";
import { MatInputModule } from "@angular/material/input";
import { ReactiveFormsModule } from '@angular/forms';

@Component({
    selector: 'app-login',
    templateUrl: 'login.component.html',
    styleUrl: 'login.component.css',
    standalone: true,
    imports: [MatCardModule, MatInputModule, MatButtonModule, ReactiveFormsModule]
})
export class LoginComponent {
    form: FormGroup = new FormGroup({
        username: new FormControl(''),
        password: new FormControl(''),
    });

    submit() {
        if (this.form.valid) {
            this.submitEM.emit(this.form.value);
        }
    }
    @Input() error: string | null = "";

    @Output() submitEM = new EventEmitter();
}