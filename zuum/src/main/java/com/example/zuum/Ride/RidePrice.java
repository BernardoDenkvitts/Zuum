package com.example.zuum.Ride;

import java.math.BigDecimal;

public class RidePrice {
     private BigDecimal price = new BigDecimal(0.8);
    
    public RidePrice(double kmDistance) {
        this.price = this.price.multiply(BigDecimal.valueOf(kmDistance));
    }

    public void increase(Float percent) {
        this.price = this.price.add(this.price.multiply(BigDecimal.valueOf(percent)));
    }
    
    public void decrease(Float percent) {
        this.price = this.price.subtract(this.price.multiply(BigDecimal.valueOf(percent)));
    }

    public BigDecimal getPrice() {
        return price;
    }

}
