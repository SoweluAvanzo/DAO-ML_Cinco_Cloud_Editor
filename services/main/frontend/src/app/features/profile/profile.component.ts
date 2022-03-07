import { Component, OnInit } from '@angular/core';
import { PasswordChangeComponent } from "./components/password-change/password-change.component";
import { PersonalInformationComponent } from "./components/personal-information/personal-information.component";

@Component({
  selector: 'cc-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
