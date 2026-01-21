# Mapbox SDK Integration Guide

## Setup Instructions

1. **Get a Mapbox Access Token:**
   - Go to https://account.mapbox.com/
   - Sign up or log in
   - Navigate to "Access tokens"
   - Create a new access token or use the default public token
   - Copy your access token

2. **Update the Mapbox Token:**
   - Open `ui/fx/controllers/MapController.java`
   - Replace `YOUR_MAPBOX_ACCESS_TOKEN` with your actual Mapbox access token
   - Example: `private static final String MAPBOX_ACCESS_TOKEN = "pk.eyJ1IjoidXNlcm5hbWUiLCJhIjoiY2x..."`

3. **JavaFX WebView Requirements:**
   - JavaFX includes WebView by default in JDK 8-10
   - For JDK 11+, you need to include JavaFX separately or use OpenJFX
   - WebView uses WebKit to render Mapbox GL JS

4. **Internet Connection:**
   - Mapbox maps require an active internet connection to load map tiles
   - The app loads Mapbox GL JS from CDN

## Features

- Interactive map display
- Marker placement
- Navigation controls (zoom, rotation)
- Customizable map style
- Location-based routing (can be extended)

## Usage

```java
// Show map at default location (Karachi)
MapController.showMap(stage);

// Show map at specific coordinates
MapController.showMap(stage, latitude, longitude);
```

## Mapbox GL JS Features

The integration uses Mapbox GL JS which provides:
- Vector maps (faster, scalable)
- Custom styling
- 3D buildings
- Route visualization
- Real-time updates

## Alternative: Native Java Map Integration

If you prefer not to use WebView, you can integrate:
- JavaFX Canvas with offline maps
- Static map images from Mapbox Static API
- Third-party Java map libraries

