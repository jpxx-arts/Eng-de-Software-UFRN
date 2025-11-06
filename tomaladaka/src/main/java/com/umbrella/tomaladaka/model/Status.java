package com.umbrella.tomaladaka.model;

public enum Status {
  PENDING,     // Waiting processing order
  PROCESSING,  // Processing order
  COMPLETED,   // Completed order
  CANCELLED,    // Cancelled order
  READY_FOR_DELIVERY, // Order already processed 
  OUT_FOR_DELIVERY  // Order with a deliveryMan assigned
}
