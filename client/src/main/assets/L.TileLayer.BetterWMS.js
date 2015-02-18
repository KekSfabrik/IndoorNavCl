/*
 * Copyright (C) 2015 Jan "KekS" M.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

/**
 * source: https://gist.github.com/rclark/6908938
 * modified by Jan "KekS" M. to allow crossDomain access and parse own objects properly
 */

L.TileLayer.BetterWMS = L.TileLayer.WMS.extend({
  
  onAdd: function (map) {
    // Triggered when the layer is added to a map.
    //   Register a click listener, then do all the upstream WMS things
    L.TileLayer.WMS.prototype.onAdd.call(this, map);
    map.on('click', this.getFeatureInfo, this);
  },
  
  onRemove: function (map) {
    // Triggered when the layer is removed from a map.
    //   Unregister a click listener, then do all the upstream WMS things
    L.TileLayer.WMS.prototype.onRemove.call(this, map);
    map.off('click', this.getFeatureInfo, this);
  },
  
  getFeatureInfo: function (evt) {
    // Make an AJAX request to the server and hope for the best
    var url = this.getFeatureInfoUrl(evt.latlng),
        showResults = L.Util.bind(this.showGetFeatureInfo, this);
    $.ajax({
      url: url,
      success: function (data) {
        var err = data.crs == null ? data : null; //typeof data === 'Object' ? null : data;
          //console.log(JSON.stringify(data));
          //Android.alert(JSON.stringify(data));
        showResults(err, evt.latlng, data);
      },
      error: function (xhr, status, error) {
        showResults(error);  
      },
        crossDomain: true,
        type: 'POST',
        dataType : 'jsonp',
        jsonpCallback : 'getJson'
    });
  },
  
  getFeatureInfoUrl: function (latlng) {
    // Construct a GetFeatureInfo request URL given a point
    var point = this._map.latLngToContainerPoint(latlng, this._map.getZoom()),
        size = this._map.getSize(),
        
        params = {
          request: 'GetFeatureInfo',
          service: 'WMS',
          srs: 'EPSG:4326',
          styles: this.wmsParams.styles,
          transparent: this.wmsParams.transparent,
          version: this.wmsParams.version,      
          format: this.wmsParams.format,
          bbox: this._map.getBounds().toBBoxString(),
          height: size.y,
          width: size.x,
          layers: this.wmsParams.layers,
          query_layers: this.wmsParams.layers,
            outputFormat : 'text/javascript',
            info_format : 'text/javascript',
            format_options : 'callback: getJson'
        };
    
    params[params.version === '1.3.0' ? 'i' : 'x'] = point.x;
    params[params.version === '1.3.0' ? 'j' : 'y'] = point.y;
    
    return this._url + L.Util.getParamString(params, this._url, true);
  },
  
  showGetFeatureInfo: function (err, latlng, content) {
    if (err) { /*console.log(err); */return; } // do nothing if there's an error

     //console.log("going to show popup at " + latlng + " with content: " + JSON.stringify(content));
    // Otherwise show the content in a popup, or something.
      var polygon = L.polygon(this.getPolygonData(content))
          .bindPopup(
          L.popup({ maxWidth: 320, closeOnClick: true, closeButton: false })
              .setLatLng(latlng)
              .setContent(this.formatOutput(content))
      )
          .on('popupclose', function() { polygon.removeFrom(this._map); })//this._map.removeLayer(polygon); })
          .addTo(this._map);
      polygon.openPopup();
  },

    formatOutput: function(content) {
        var output = "";
        for (i = 0, k = content.features.length; i<k; i++) {
            if (i != 0) {
                output += "<br>";
            }
            output += "<b>"+content.features[0].properties.raumnr+"</b><br>"+content.features[0].properties.name;
        }
        return output;
    },

    getPolygonData: function(content) {
        var output = [];
        for (var i = 0, k = content.features[0].geometry.coordinates[0][0].length; i < k; i++) {
            var arr = content.features[0].geometry.coordinates[0][0][i];
            output.push([arr[1], arr[0]]);

        }
        return output;
    }
});

L.tileLayer.betterWms = function (url, options) {
  return new L.TileLayer.BetterWMS(url, options);  
};