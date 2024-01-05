// server.js
const express = require('express');
const bodyParser = require('body-parser');
const axios = require('axios');
const cors = require('cors');
const mongoose = require("mongoose");
const app = express();
const PORT = 5000;
const crouter = require("./routes/customer-routes");

app.use(cors());
app.use(bodyParser.json());
app.use(express.json());
app.use("/customers", crouter);

app.post('/ip-geolocation', async (req, res) => {
  try {
    const response = await axios.post('http://ip-api.com/batch', [
      { query: req.body.query },
    ]);
    const geolocationData = response.data[0];
    res.json({
      country: geolocationData.country,
      regionName: geolocationData.regionName,
      city: geolocationData.city,
      zip: geolocationData.zip,
      isp: geolocationData.isp,
      org: geolocationData.org,
      as: geolocationData.as,
      lat: geolocationData.lat,
      lon: geolocationData.lon,
    });
  } catch (error) {
    console.error('Error fetching geolocation data:', error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
});

mongoose
  .connect(
    "mongodb+srv://admin:admin123@cluster0.vvgk1lb.mongodb.net/gorilla?retryWrites=true&w=majority"
  )
  .then(() => console.log("Connected To Database"))
  .then(() => {
    app.listen(3001);
  })
  .catch((err) => console.log(err));

app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`);
});

