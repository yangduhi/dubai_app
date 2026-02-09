# 🍪 Dubai Cookie Finder

GPS 기반 두바이 쫀득 쿠키 재고 확인 앱

## 🚀 Quick Start

### 1. API Key 설정

```powershell
# local.properties 생성
cd android
copy local.properties.template local.properties
# 그 다음 local.properties를 열어 MAPS_API_KEY에 실제 키 입력
```

### 2. Android Studio에서 열기

`File > Open > d:\vscode\dubai_app\android` 선택

### 3. 빌드 및 실행

- ▶️ Run 버튼 클릭
- 에뮬레이터 또는 실기기에서 확인

---

## 📁 프로젝트 구조

```
android/
├── app/src/main/
│   ├── java/com/example/dubaicookiefinder/
│   │   ├── data/
│   │   │   ├── model/Store.kt
│   │   │   └── repository/
│   │   │       ├── MockData.kt
│   │   │       └── StoreRepository.kt
│   │   └── ui/
│   │       ├── components/
│   │       │   ├── StockBadge.kt
│   │       │   ├── StoreCard.kt
│   │       │   ├── MarkerInfoWindow.kt
│   │       │   └── GlassModifier.kt
│   │       ├── screens/MapScreen.kt
│   │       ├── theme/
│   │       │   ├── Color.kt (Apple Style)
│   │       │   ├── Type.kt (Apple Style)
│   │       │   ├── Shape.kt
│   │       │   └── Theme.kt
│   │       └── viewmodel/MapViewModel.kt
│   └── res/
└── gradle/libs.versions.toml
```

## 🎨 Design System

- **Color Palette**: Apple Website 스타일
- **Typography**: SF Pro 느낌의 Bold Title
- **Shapes**: 20-24dp Corner Radius
- **Effects**: Glassmorphism (API 31+)

## ✅ Features

- [x] Google Maps 통합
- [x] 위치 권한 처리
- [x] 재고 상태별 마커 색상
- [x] BottomSheet 매장 리스트
- [x] Apple 스타일 UI

---

Made with 🍪 by Dubai Cookie Team
