import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { MatSliderChange } from '@angular/material/slider';
import { LightingService } from '../service/lighting.service';

@Component({
  selector: 'app-lighting-control',
  templateUrl: './lighting-control.component.html',
  styleUrls: ['./lighting-control.component.scss']
})
export class LightingControlComponent implements OnInit {

  constructor(private service: LightingService) { }

  redValue = 0;
  greenValue = 0;
  blueValue = 0;

  time = 1000;

  ngOnInit(): void {
    this.service.getCurrentColor().subscribe(color => {
      this.redValue = color.r;
      this.greenValue = color.g;
      this.blueValue = color.b;
    })
  }

  public onRedChange(m: MatSliderChange) {
    this.redValue = m.value;
  }

  public onGreenChange(m: MatSliderChange) {
    this.greenValue = m.value;
  }

  public onBlueChange(m: MatSliderChange) {
    this.blueValue = m.value;
  }

  public onSend() {
    this.service.setColor({ r: this.redValue, g: this.greenValue, b: this.blueValue }, this.time);
  }

  public onReset() {
    this.service.resetColor();
  }

}
