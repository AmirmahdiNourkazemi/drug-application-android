package com.approagency.drug.utils


val provinces = listOf(
    Province("0", "همه استان ها"),
    Province("1", "اصفهان"),
    Province("40", "تهران"),
    Province("137", "آذربایجان شرقی"),
    Province("138", "آذربایجان غربی"),
    Province("139", "اردبیل"),
    Province("140", "البرز"),
    Province("141", "ایلام"),
    Province("142", "بوشهر"),
    Province("143", "چهارمحال و بختیاری"),
    Province("144", "خراسان جنوبی"),
    Province("145", "خراسان رضوی"),
    Province("146", "خراسان شمالی"),
    Province("147", "خوزستان"),
    Province("148", "زنجان"),
    Province("149", "سمنان"),
    Province("150", "سیستان و بلوچستان"),
    Province("151", "فارس"),
    Province("152", "قزوین"),
    Province("153", "قم"),
    Province("154", "کردستان"),
    Province("155", "کرمان"),
    Province("156", "کرمانشاه"),
    Province("157", "کهگیلویه و بویراحمد"),
    Province("158", "گلستان"),
    Province("159", "گیلان"),
    Province("160", "لرستان"),
    Province("161", "مازندران"),
    Province("162", "مرکزی"),
    Province("163", "هرمزگان"),
    Province("164", "همدان"),
    Province("165", "یزد")
)

data class Province(
    val id: String,
    val name: String
)