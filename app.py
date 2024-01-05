from flask import Flask, request, jsonify, render_template
import pickle
from flask_cors import CORS
import numpy as np
import requests
import pymongo
from datetime import datetime
import pandas as pd
import math

app = Flask(__name__)
CORS(app)

def delete_record(uid):
    client = pymongo.MongoClient("mongodb+srv://achucod03:achintya%40mango@krypton.dc3eerr.mongodb.net/")
    db = client.magic
    collection = db.users
    collection.delete_one({"uid": uid})

def get_lat_lon_from_ip(ip_address):
    try:
        response = requests.post("http://ip-api.com/batch", json=[{"query": ip_address}]).json()
        if not response or 'lat' not in response[0] or 'lon' not in response[0]:
            print(f"Unable to get location details for IP address {ip_address}")
            return None

        latitude = response[0]['lat']
        longitude = response[0]['lon']

        return latitude, longitude
    except Exception as e:
        print(f"Error retrieving data: {e}")
        return None
    
def get_loc_from_id(uid):
    client = pymongo.MongoClient("mongodb+srv://achucod03:achintya%40mango@krypton.dc3eerr.mongodb.net/")
    db = client.magic
    collection = db.uid_loc
    data = collection.find({"uid": uid})

    for _ in data:
        _.pop('_id')
        return _
    
def get_score_from_id(uid):
    client = pymongo.MongoClient("mongodb+srv://achucod03:achintya%40mango@krypton.dc3eerr.mongodb.net/")
    db = client.magic
    collection = db.users
    data = collection.find({"uid": uid})

    for _ in data:
        _.pop('_id')
        return _
    
def update_score(uid, score):
    client = pymongo.MongoClient("mongodb+srv://achucod03:achintya%40mango@krypton.dc3eerr.mongodb.net/")
    db = client.magic
    collection = db.users
    collection.delete_one({"uid": uid})
    collection.insert_one({"uid": uid, "fraud_rate": score})
                                                  
def update_user(uid, time, lat, long):
    client = pymongo.MongoClient("mongodb+srv://achucod03:achintya%40mango@krypton.dc3eerr.mongodb.net/")
    db = client.magic
    collection = db.uid_loc
    collection.delete_one({"uid": uid})
    collection.insert_one({"uid": uid, "time": time, "lat": lat, "long": long})

def calculate_distance(lat_prev, long_prev, lat_current, long_current):
    R = 6371  
    lat_prev_rad = math.radians(lat_prev)
    long_prev_rad = math.radians(long_prev)
    lat_current_rad = math.radians(lat_current)
    long_current_rad = math.radians(long_current)

    delta_lat = lat_current_rad - lat_prev_rad
    delta_long = long_current_rad - long_prev_rad

    a = math.sin(delta_lat / 2) ** 2 + math.cos(lat_prev_rad) * math.cos(lat_current_rad) * math.sin(delta_long / 2) ** 2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))

    distance = R * c
    return distance

def convert_to_compatible(input, target):

    print(input)
    NOW = datetime.now()


    target["cc_num"] = get_score_from_id("'" + input["cc_num"])["fraud_rate"]
    target["merchant"] = get_score_from_id(input["merchant"])["fraud_rate"]
    target["amt"] = input["amt"]

    lat, long = get_lat_lon_from_ip(input["sender_ip"])
    target["lat"] = lat
    target["long"] = long


    m_lat, m_long = get_lat_lon_from_ip(input["merch_ip"])
    target["merch_lat"] = m_lat
    target["merch_long"] = m_long


    prev_time = get_loc_from_id("'" + input["cc_num"])["time"]
    prev_lat = get_loc_from_id("'" + input["cc_num"])["lat"]
    prev_long = get_loc_from_id("'" + input["cc_num"])["long"]

    prev_merch_time = get_loc_from_id(input["merchant"])["time"]
    prev_merch_lat = get_loc_from_id(input["merchant"])["lat"]
    merch_prev_long = get_loc_from_id(input["merchant"])["long"]

    target["cc_time_diff"] = (pd.to_datetime(NOW) - pd.to_datetime(prev_time)).total_seconds()
    target["distance_diff"] = calculate_distance(prev_lat, prev_long, target["lat"], target["long"])

    target["merch_time_diff"] = (pd.to_datetime(NOW) - pd.to_datetime(prev_merch_time)).total_seconds()
    target["merch_distance_diff"] = calculate_distance(prev_merch_lat, merch_prev_long, target["merch_lat"], target["merch_long"])

    update_user("'" + input["cc_num"], NOW, target["lat"], target["long"])
    update_user(input["merchant"], NOW, target["merch_lat"], target["merch_long"])


  # Enable CORS for all routes

model = pickle.load(open('NiceModel.sav', 'rb'))

@app.route('/')
def home():
    return render_template('index.html')
# Update the predict route in Flask
@app.route('/predict', methods=['POST'])
def predict():
    try:
        print(request.form)
        in_features = request.form.to_dict()
        out_features = {}

        convert_to_compatible(in_features, out_features)

        float_features = [float(x) for x in out_features.values()]

        final_features = np.array(float_features).reshape(1, -1)

        if final_features.shape[1] != model.n_features_in_:
            return render_template('index.html', prediction_text='Invalid number of features')

        prediction = model.predict(final_features)
        output = round(prediction[0], 2)

        return jsonify(str(output))

    except Exception as e:
        print(f"Error predicting: {e}")
        return jsonify({"error": "Error predicting"}), 500


@app.route('/predict_api', methods=['POST'])
def predict_api():
    '''
    For direct API calls through request
    '''
    data = request.get_json(force=True)
    prediction = model.predict([np.array(list(data.values()))])

    # Convert the prediction to a standard Python data type
    output = prediction[0].item()  # Convert NumPy int64 to Python int

    return jsonify(output)


if __name__ == "__main__":
    app.run(debug=True)