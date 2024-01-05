  const Customer = require("../model/Customer");

  const getAllCustomers = async (req, res, next) => {
    try {
      const customers = await Customer.find();
      if (!customers || customers.length === 0) {
        return res.status(404).json({ message: "No customers found" });
      }
      return res.status(200).json({ customers });
    } catch (err) {
      console.error(err);
      return res.status(500).json({ message: "Internal Server Error" });
    }
  };

  const getCustomerById = async (req, res, next) => {
    const id = req.params.id;
    try {
      const customer = await Customer.findById(id);
      if (!customer) {
        return res.status(404).json({ message: "No customer found" });
      }
      return res.status(200).json({ customer });
    } catch (err) {
      console.error(err);
      return res.status(500).json({ message: "Internal Server Error" });
    }
  };

  const addCustomer = async (req, res, next) => {
    const {
      customerId,
      customerName,
      customerPOC,
      customerAddress,
      customerEmail,
      customerPhone,
      customerType,
      customerCategory,
    } = req.body;
    try {
      const customer = new Customer({
        customerId,
        customerName,
        customerPOC,
        customerAddress,
        customerEmail,
        customerPhone,
        customerType,
        customerCategory,
      });
      await customer.save();
      return res.status(201).json({ customer });
    } catch (err) {
      console.error(err);
      return res.status(500).json({ message: "Unable to add customer" });
    }
  };

  const updateCustomer = async (req, res, next) => {
    const id = req.params.id;
    const {
      customerId,
      customerName,
      customerPOC,
      customerAddress,
      customerEmail,
      customerPhone,
      customerType,
      customerCategory,
    } = req.body;
    try {
      let customer = await Customer.findByIdAndUpdate(id, {
        customerId,
        customerName,
        customerPOC,
        customerAddress,
        customerEmail,
        customerPhone,
        customerType,
        customerCategory,
      });
      if (!customer) {
        return res.status(404).json({ message: "Unable to update. Customer not found" });
      }
      customer = await customer.save();
      return res.status(200).json({ customer });
    } catch (err) {
      console.error(err);
      return res.status(500).json({ message: "Internal Server Error" });
    }
  };

  const deleteCustomer = async (req, res, next) => {
    const id = req.params.id;
    try {
      const customer = await Customer.findByIdAndDelete(id);
      if (!customer) {
        return res.status(404).json({ message: "Unable to delete. Customer not found" });
      }
      return res.status(200).json({ message: "Customer successfully deleted" });
    } catch (err) {
      console.error(err);
      return res.status(500).json({ message: "Internal Server Error" });
    }
  };

  module.exports = {
    getAllCustomers,
    addCustomer,
    getCustomerById,
    updateCustomer,
    deleteCustomer,
  };
