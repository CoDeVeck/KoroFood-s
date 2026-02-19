import { Component, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { GraficoUnoComponent } from '../graficos/grafico-uno/grafico-uno.component';
import { GraficoDosComponent } from '../graficos/grafico-dos/grafico-dos.component';
import { GraficoTresComponent } from '../graficos/grafico-tres/grafico-tres.component';
import { GraficoCuatroComponent } from '../graficos/grafico-cuatro/grafico-cuatro.component';
import { GraficoCincoComponent } from '../graficos/grafico-cinco/grafico-cinco.component';
import { GraficoSeisComponent } from '../graficos/grafico-seis/grafico-seis.component';

import { GridStack } from 'gridstack';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    GraficoUnoComponent,
    GraficoDosComponent,
    GraficoTresComponent,
    GraficoCuatroComponent,
    GraficoCincoComponent,
    GraficoSeisComponent,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements AfterViewInit {
  @ViewChild('grid') gridEl!: ElementRef;
  grid!: GridStack;

  ngAfterViewInit() {
    this.grid = GridStack.init(
      {
        column: 4,
        cellHeight: 200,
        margin: 15,
        float: true,
        resizable: { handles: 'all' },
      },
      this.gridEl.nativeElement,
    );
  }
}
