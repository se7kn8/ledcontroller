import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Color } from '../entities/color';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators'

@Injectable({
  providedIn: 'root'
})
export class LightingService {

  private serverUrl = 'http://localhost:8080/control'

  constructor(private http: HttpClient) { }

  //ledcontroller:8080/control/lighting?color=000000&time=500

  public resetColor() {
    this.http.post(`${this.serverUrl}/lighting/reset`, "", { responseType: 'text' as 'json' }).subscribe();
  }

  public setColor(color: Color, time: number) {
    let hexColor = this.rgbToHex(color.r, color.g, color.b);
    if(time != 0){
      this.http.post(`${this.serverUrl}/lighting?color=${hexColor}&time=${time}`, "").subscribe();
    }else {
      this.http.post(`${this.serverUrl}/lighting?color=${hexColor}`, "").subscribe();
    }
  }

  public getCurrentColor(): Observable<Color> {
    return this.http.get(`${this.serverUrl}/lighting`, { responseType: 'text' as 'json' }).pipe(map((response: string) => {

      let color: Color = {
        r: parseInt(response.substring(1, 3), 16),
        g: parseInt(response.substring(3, 5), 16),
        b: parseInt(response.substring(5, 7), 16)
      };
      return color;
    }));
  }

  private rgbToHex(red: number, green: number, blue: number): string {
    return ((1 << 24) + (red << 16) + (green << 8) + blue).toString(16).slice(1);
  }



}
