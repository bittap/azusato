package com.my.azusato.program;

public class EnterDelete {

	public static void main(String[] args) {
		String content = "								風薫るさわやかな季節となりましたが、いかがお過ごしでしょうか。\r\n"
				+ "								さて、建築中でした私どもの住まいもようやく完成いたしまして、\r\n"
				+ "								このほど転居いたしました。\r\n"
				+ "								都心からは遠くなりましたが、海沿いということもあり\r\n"
				+ "								窓から見える景色の良さは格別です。\r\n"
				+ "								<br>\r\n"
				+ "								<br>\r\n"
				+ "								\r\n"
				+ "								つきましては、5月24日の日曜日に拙宅にて、\r\n"
				+ "								ささやかながら新築祝いを催したいと存じます。\r\n"
				+ "								格別の用意もございませんが、何卒ご都合お繰り合わせの上、\r\n"
				+ "								おいでいただけますようお願い申し上げます。\r\n"
				+ "								\r\n"
				+ "								略図を添えましたが、わかりにくいようでしたらお迎えに参りますので、駅からでもお電話くださ";
		
		String result = content.replaceAll("[\\r|\\n||\\t]", "");
		
		System.out.println(result);
	}
}
