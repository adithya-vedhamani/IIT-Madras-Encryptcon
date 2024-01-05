const express = require("express");
const crouter = express.Router();
const Customer = require("../model/Customer");
const customersController = require("../controllers/customers-controller");

crouter.get("/", customersController.getAllCustomers);
crouter.post("/", customersController.addCustomer);
crouter.get("/:id", customersController.getCustomerById);
crouter.put("/:id", customersController.updateCustomer);
crouter.delete("/:id", customersController.deleteCustomer);

module.exports = crouter;
