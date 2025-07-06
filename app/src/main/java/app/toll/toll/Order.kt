package com.zippr.zippr

data class Order(
    var orderId: String?,
    var userId: String?,
    var driverId: String?,
    var pickupLocation: Location?,
    var dropoffLocation: Location?,
    var status: String?,
    var fareEstimate: Double?,
    var currency: String?,
    var requestedTime: String?,
    var paymentMethod: String?,
    var distanceKm: Double?,
    var durationMin: Int?,
    var platformFeePaid: Boolean?,
    var notes: String?,
    var routePolyline: String?,
    var serviceMode: String?,
    var serviceClass: String?
)
