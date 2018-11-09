# WeatherCalendar
A Simple App with a 5-day Weather Forecast.

This app makes use of the free, 5-day/3-hour forecast <b>OpenWeatherMaps API</b>:

https://openweathermap.org/api

The key for this API is omitted from this repository.  To add the API key, you must create a new xml file:

WeatherCalendar/app/src/main/res/values/<b>secrets.xml</b>

and add the following resource:

`<resources>`</br>
    `<string name="OpenWeatherMaps_API_Key_Secret">YOUR_API_KEY_HERE</string>`
    </br>
`</resources>`

