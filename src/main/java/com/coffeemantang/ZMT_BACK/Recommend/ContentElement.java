package com.coffeemantang.ZMT_BACK.Recommend;


public class ContentElement {
    private static final String[] elements = {
            "포유류", "조류", "어류", "조개류", "두족류", "채소류", "해조류", "달걀류",
            "유제품", "곡류", "면", "빵", "커피", "과일",
            "매운맛", "짠맛", "단맛", "신맛", "쓴맛", "감칠맛", "고소한맛", "무미",
            "끓이기", "찌기", "조리기", "굽기", "튀기기", "볶기", "데치기", "절이기",
            "재우기", "무치기", "베이킹", "음료", "오븐", "생", "굳히기", "말리기", "훈제",
            "차가운", "미온", "뜨거운",
            "한식", "중식", "양식", "아시안", "일식", "디저트", "카페", "패스트푸드",
            "치킨", "피자", "야식", "분식"
    };
    public static String[] getElements(){
        return elements;
    }
}
