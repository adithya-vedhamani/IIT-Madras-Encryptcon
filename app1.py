from flask import Flask, request, jsonify, render_template
import pickle
from flask_cors import CORS
import numpy as np

app = Flask(__name__)
CORS(app)  # Enable CORS for all routes

model = pickle.load(open('NiceModel.sav', 'rb'))

@app.route('/')
def home():
    return render_template('index.html')

@app.route('/predict', methods=['POST'])
def predict():
    '''
    For rendering results on HTML GUI
    '''
    float_features = [float(x) for x in request.form.values()]
    final_features = np.array(float_features).reshape(1, -1)

    # Check the number of features expected by the model
    if final_features.shape[1] != model.n_features_in_:
        return render_template('index.html', prediction_text='Invalid number of features')

    prediction = model.predict(final_features)

    output = round(prediction[0], 2)

    return render_template('index.html', prediction_text='The Transaction is: $ {}'.format(output))

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
