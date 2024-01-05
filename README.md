# IIT Madras - Encryptcon Hackathon
# PS3 - Behavioral Analytics for Detecting Anomalies in Financial Transactions
***
## Team Members
| Name              | Role                  |
|-------------------|-----------------------|
| Vishwa Kumaresh   | Team Lead, Android Dev |
| Adithya Vedhamani | Software Developer    |
| Achintya L        | Backend Design and API |
| Lohith K Saradhi  | Model Development     |
***
# Demo Video
[![Watch the video](https://img.youtube.com/vi/T-D1KVIuvjA/maxresdefault.jpg)](https://youtu.be/T-D1KVIuvjA)


***
# Project Setup
- Installations :  **Give bash Files**
- Configuration Files or commands


Created: January 5, 2024 3:49 PM
Status: Open
Updated: January 5, 2024 4:56 PM

## Setup

Clone the repository (if the name is changed, please change accordingly in the [krypton.sh](http://krypton.sh) file)

```jsx
chmod a+x krypton.sh

./krypton.sh
```

[Link to Models](https://drive.google.com/drive/folders/1O1HlEvthKPsyAOxHBqB1dQGuMC7ydD5v?usp=drive_link)

### 

# Project Overview

Every day, millions of people trust their hard-earned money in banking systems, yet this very convenience has opened a pandora's box of vulnerabilities. 5% of global corporate revenue, translating to a mind-boggling $4.7 trillion (According to a report by ACFE), is pocketed by fraudsters. Astonishingly, 93% of banking-related fraud happens online (According to the Financial Crime Report Q2 2021). This shows the pressing need for an improved Fraud Detection System.

We propose a novel behavioral analytics system as a SAAS, exploiting ML algorithms for real-time detection of fraudulent transactions. The Machine Learning model will analyze whether the incoming transaction is fraudulent or legitimate. Upon raising the flag for the fraudulent ones, the system also tracks the criminal's IP address to estimate their location for further action. Being a SAAS enables the system to also send alerts to the clients to be wary of  criminals.

## Functionalities

- **Real-time Software:-** Developing a user-friendly software for Behavioral Analytics.
- **Real-Time API :**  Exposing the software as an API facilitates easy integration with various banking applications
- **Random Forest  model:-** Utilizes Random Forest for effective and real-time anomaly detection.
- **Feature Enhancements**
    - Geolocating criminals using their IP
    - Network analysis post a fraud event to track the stolen money.
    - Spam and Phishing detection for mobile devices.
    - Hash-based user authentication for the service

## Tech Stack

- ReactJS
- MongoDB
- Python
- Express
- Node
- Flask
- Fast API
- Bootstrap

## UI and App

![Image1](https://github.com/Vishwa-docs/IIT-Madras-Encryptcon/blob/main/resources/IMG-20240105-WA0007.jpg)

![Image2](https://github.com/Vishwa-docs/IIT-Madras-Encryptcon/blob/main/resources/IMG-20240105-WA0016.jpg)

## Data and Model

### Data

The data used for training our models was sourced from an open source synthetic dataset on kaggle : 

- [https://www.kaggle.com/datasets/kartik2112/fraud-detection/data](https://www.kaggle.com/datasets/kartik2112/fraud-detection/data)

Our novelty comes with our incorporation of time and distance-based measures to train our model to better detect fraudulent transactions. We compute the distance between the last known location of an account and the current location along with the time interval between the transactions to allow the model to learn the anomalous behavior of certain transactions. This location data is sourced from the public IP information available with the transaction packet. 

### Model

The model we chose for our approach is the ever-resilient Random Forest. Through a series of attempts, random forest proved to be better than the others for our carefully curated dataset.

### Deployment

The end deliverable is packaged as a React-Flask web app and a Fast API that allows the users to check if a given transaction is fraudulent. The location data is directly taken from the IP information given. The distance metric is calculated after referring to the last known location and time stamp.




