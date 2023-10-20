import { Component } from '@angular/core';
import { faBug, faBook, faHome } from '@fortawesome/free-solid-svg-icons';
import { faGitlabSquare } from '@fortawesome/free-brands-svg-icons';

@Component({
  selector: 'cc-app-footer',
  templateUrl: './app-footer.component.html',
  styleUrls: ['app-footer.component.scss']
})
export class AppFooterComponent {
  faGitlabSquare = faGitlabSquare;
  faHome = faHome;
  faBug = faBug;
  faBook = faBook;
}
