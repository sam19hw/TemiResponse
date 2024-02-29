package com.sam19hw.temiresponse.data;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Colour {

        @SerializedName("r")
        @Expose
        private Double r;
        @SerializedName("g")
        @Expose
        private Double g;
        @SerializedName("b")
        @Expose
        private Double b;
        @SerializedName("a")
        @Expose
        private Double a;

        public Double getR() {
            return r;
        }

        public void setR(Double r) {
            this.r = r;
        }

        public Double getG() {
            return g;
        }

        public void setG(Double g) {
            this.g = g;
        }

        public Double getB() {
            return b;
        }

        public void setB(Double b) {
            this.b = b;
        }

        public Double getA() {
            return a;
        }

        public void setA(Double a) {
            this.a = a;
        }

    }