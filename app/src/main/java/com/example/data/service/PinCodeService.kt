package com.example.data.service

data class PinServiceabilityResult(
    val pinCode: String,
    val isServiceable: Boolean,
    val regionLabel: String,
    val estimatedDays: String,
    val codAvailable: Boolean,
    val courierPartner: String,
    val message: String
)

object PinCodeService {
    private val metroPins = mapOf(
        "110" to "New Delhi, Delhi NCR",
        "122" to "Gurugram, Haryana",
        "201" to "Noida, Uttar Pradesh",
        "400" to "Mumbai, Maharashtra",
        "411" to "Pune, Maharashtra",
        "560" to "Bengaluru, Karnataka",
        "500" to "Hyderabad, Telangana",
        "600" to "Chennai, Tamil Nadu",
        "682" to "Kochi, Kerala",
        "700" to "Kolkata, West Bengal",
        "302" to "Jaipur, Rajasthan",
        "380" to "Ahmedabad, Gujarat",
        "395" to "Surat, Gujarat",
        "160" to "Chandigarh",
        "226" to "Lucknow, Uttar Pradesh",
        "751" to "Bhubaneswar, Odisha",
        "781" to "Guwahati, Assam",
        "800" to "Patna, Bihar",
        "462" to "Bhopal, Madhya Pradesh",
        "452" to "Indore, Madhya Pradesh"
    )

    val indianStates = listOf(
        "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
        "Delhi", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand",
        "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur",
        "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajasthan",
        "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh",
        "Uttarakhand", "West Bengal"
    )

    fun checkPinCode(rawPin: String): PinServiceabilityResult {
        val pin = rawPin.trim()
        if (!pin.matches(Regex("^[1-8][0-9]{5}$"))) {
            return PinServiceabilityResult(
                pinCode = pin,
                isServiceable = false,
                regionLabel = "",
                estimatedDays = "",
                codAvailable = false,
                courierPartner = "",
                message = "Please enter a valid 6-digit Indian PIN code (starts with 1–8)."
            )
        }

        val prefix3 = pin.substring(0, 3)
        val isMetro = metroPins.containsKey(prefix3)
        val region = metroPins[prefix3] ?: when (pin.first()) {
            '1' -> "Delhi NCR / Punjab / Haryana / J&K / HP"
            '2' -> "Uttar Pradesh / Uttarakhand"
            '3' -> "Rajasthan / Gujarat"
            '4' -> "Maharashtra / Goa / Madhya Pradesh / Chhattisgarh"
            '5' -> "Telangana / Andhra Pradesh / Karnataka"
            '6' -> "Tamil Nadu / Kerala / Puducherry"
            '7' -> "West Bengal / Odisha / North-East India"
            '8' -> "Bihar / Jharkhand"
            else -> "Pan-India"
        }

        val isRemoteNorthEast = pin.startsWith("79") || pin.startsWith("194")
        val eta = when {
            isMetro -> "2–3 business days"
            isRemoteNorthEast -> "5–7 business days"
            else -> "3–5 business days"
        }
        val courier = if (isMetro) "BlueDart Luxury Air" else "Delhivery Pan-India Express"

        return PinServiceabilityResult(
            pinCode = pin,
            isServiceable = true,
            regionLabel = region,
            estimatedDays = eta,
            codAvailable = true,
            courierPartner = courier,
            message = "Deliverable to $region in $eta via $courier. COD & Express available."
        )
    }
}
