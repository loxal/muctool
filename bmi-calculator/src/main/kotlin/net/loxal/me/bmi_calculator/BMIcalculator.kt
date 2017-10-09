/*
 * MUCtool Web Toolkit
 *
 * Copyright 2017 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.loxal.me.bmi_calculator

import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLImageElement
import org.w3c.dom.HTMLInputElement
import kotlin.browser.document
import kotlin.js.Math

class BMIcalculator {
    private val bmiChart = document.getElementById("bmiChart") as HTMLImageElement
    private val bmiMarker = document.getElementById("bmiMarker") as HTMLImageElement
    private val weight = document.getElementById("weight") as HTMLInputElement
    private val height = document.getElementById("height") as HTMLInputElement
    private val metric = document.getElementById("metric") as HTMLInputElement
    private val imperial = document.getElementById("imperial") as HTMLInputElement
    private val heightUnitLabel = document.getElementById("heightUnitLabel") as HTMLElement
    private val weightUnitLabel = document.getElementById("weightUnitLabel") as HTMLElement
    private val bmi = document.getElementById("bmiValue") as HTMLDivElement
    private val lbInKg = 0.45359237
    private val inInCm = 2.54
    private var weightInKg: Double = weight.value.toDouble()
    private var heightInCm: Double = height.value.toDouble()
    private var isMetricMeasurement = true

    private fun initListeners() {
        weight.onchange = {
            showBMI()
        }

        height.onchange = {
            showBMI()
        }

        imperial.onchange = {
            calculateImperialBMI()
        }

        metric.onchange = {
            calculateMetricBMI()
        }
    }

    private fun setMetaValues() {
        if (isMetricMeasurement) {
            setMetric()
        } else {
            convertMetricToImperial()
        }
    }

    private fun changeToMetric() {
        metric.checked = true
        isMetricMeasurement = true

        setMetricLabels()
        val cmTooPrecise = convertInToCm(height.value.toDouble()).toString()
        val kgTooPrecise = convertLbToKg(weight.value.toDouble()).toString()
        height.value = cmTooPrecise
        weight.value = kgTooPrecise
    }

    private fun changeToImperial() {
        imperial.checked = true
        isMetricMeasurement = false

        setImperialLabels()
        val inTooPrecise = convertCmToIn(height.value.toDouble()).toString()
        val lbTooPrecise = convertKgToLb(weight.value.toDouble()).toString()
        height.value = inTooPrecise
        weight.value = lbTooPrecise
    }

    private fun calculateMetricBMI() {
        changeToMetric()
        showBMI()
    }

    private fun calculateImperialBMI() {
        changeToImperial()
        showBMI()
    }

    private fun calculateBMI(): Double {
        setMetaValues()

        return weightInKg / Math.pow(heightInCm, 2.0) * 1e4
    }

    private fun showBMI() {
        val bmiValue = calculateBMI().toString()
        bmi.textContent = "BMI: ${bmiValue.substring(0, 5)}" // TODO good enough for 99.9% (proper rounding is not supported by Kotlin yet)

        putBMImarker(weight.value.toDouble(), height.value.toDouble())
    }

    private fun setMetric() {
        heightInCm = height.value.toDouble()
        weightInKg = weight.value.toDouble()
    }

    private fun convertMetricToImperial() {
        heightInCm = convertInToCm(height.value.toDouble())
        weightInKg = convertLbToKg(weight.value.toDouble())
    }

    private fun convertCmToIn(cm: Double) = cm / inInCm

    private fun convertKgToLb(kg: Double) = kg / lbInKg

    private fun convertInToCm(inches: Double) = inches * inInCm

    private fun convertLbToKg(lb: Double) = lb * lbInKg

    private fun kgToXscale(kg: Double): Double {
        val chartLeftZero = 10
        val chartOffsetForScale = 39
        val kgStart = 40.0
        val xPerKg = 4.116
        val visibleWeight = kg - kgStart

        return (chartLeftZero + chartOffsetForScale) + (visibleWeight * xPerKg)
    }

    private fun cmToYscale(cm: Double): Double {
        val chartGround = bmiChart.height
        val chartOffsetForScale = 31.5
        val cmStart = 148

        val yPerCm = 7.62
        val visibleHeight = cm - cmStart

        return (chartGround - chartOffsetForScale) - (visibleHeight * yPerCm)
    }

    private fun putBMImarker(kg: Double, cm: Double) {
        val x = kgToXscale(kg)
        val y = cmToYscale(cm)

        bmiMarker.style.cssText = "position: absolute; top: ${y}px; left: ${x}px;"
    }

    init {
        setBMIChartSize()
        initListeners()

        showBMI()
    }

    private fun setBMIChartSize() {
        bmiChart.width = 590
        bmiChart.height = 480
    }

    private fun setMetricLabels() {
        heightUnitLabel.textContent = "cm:"
        weightUnitLabel.textContent = "kg:"
    }

    private fun setImperialLabels() {
        heightUnitLabel.textContent = "in:"
        weightUnitLabel.textContent = "lb:"
    }
}