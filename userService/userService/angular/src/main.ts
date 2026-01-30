import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

bootstrapApplication(AppComponent, appConfig)
  .then(() => {
    console.log('Aplicación iniciada correctamente');
  })
  .catch((err) => {
    console.error('Error al iniciar aplicación:', err);
    console.error('Stack trace:', err.stack);
  });
