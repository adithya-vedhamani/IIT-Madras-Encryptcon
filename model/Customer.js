const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const customerSchema = new Schema({
  customerId: {
    type: String,
    required: true,
  },
  customerName: {
    type: String,
    required: true,
  },
  customerPOC: {
    type: String,
    required: true,
  },
  customerAddress: {
    type: String,
    required: true,
  },
  customerEmail: {
    type: String,
    required: true,
    unique: true, 
  },
  customerPhone: {
    type: String,
    required: true,
  },
  customerType: {
    type: String,
    required: true,
  },
  customerCategory: {
    type: String,
    required: true,
  },
});

module.exports = mongoose.model('Customer', customerSchema);
