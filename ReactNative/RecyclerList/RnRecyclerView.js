'use strict';

import React, { Component } from 'react';
import {
  AppRegistry,
  AndroidToast,
  requireNativeComponent,
  View,
  PropTypes,
  Text,
  DeviceEventEmitter
} from 'react-native';
var cloneReferencedElement = require('react-clone-referenced-element');
var StaticRenderer = require('StaticRenderer');
var StyleSheetPropType = require('StyleSheetPropType');
var ViewStylePropTypes = require('ViewStylePropTypes');

class RnRecyclerView extends React.Component{
    constructor(props) {
    super(props);
    this._needViews = this._needViews.bind(this);
    this._endReached = this._endReached.bind(this);
  }
  
  componentDidMount(){
      DeviceEventEmitter.addListener(
                       'need_views',
                       this._needViews);
     DeviceEventEmitter.addListener(
                       'end_reached',
                       this._endReached);
  }
  
  _needViews(e:Event){
      if(this.props.needViews){
          this.props.needViews(e);
      }
  }
  
  _endReached(e:Event){
       if(this.props.endReached){
          this.props.endReached();
      }
  }
  
  shouldComponentUpdate(nextProps, nextState) {
    return false;
 }
  
  render(){
      var children = [];

      //genar children
      if(this.props.renderTypeView){
          var views = this.props.childViewCount;
          for(var ii=0; ii<views; ii++){
              var row = <StaticRenderer
              key={"r_"+ii}
              shouldUpdate={false}
              render={this.props.renderTypeView.bind({}, ii)}/>
              children.push(row);
          }
      }
       return cloneReferencedElement(<NativeRnRecyclerView
      {...View.props}
      style={StyleSheetPropType(ViewStylePropTypes), this.props.style}
      renderTypeView={this.props.renderTypeView}
      emptyViewHeight={this.props.emptyViewHeight}
      childViewCount={this.props.childViewCount}
      needViews={this.props.needViews}
      endReached={this.props.endReached}
      viewTypesMap={this.props.viewTypesMap}
      itemClickable={this.props.itemClickable}
      />, {}, children);
  }
}

 RnRecyclerView.propTypes= {
      ...View.propTypes,
    needViews: React.PropTypes.func,
    endReached: React.PropTypes.func,
    renderTypeView: React.PropTypes.func.isRequired,
    emptyViewHeight: React.PropTypes.number,
    childViewCount: React.PropTypes.number,
    itemClickable: React.PropTypes.bool,
    viewTypesMap: React.PropTypes.object,
    style:StyleSheetPropType(ViewStylePropTypes)
  };

var NativeRnRecyclerView = requireNativeComponent('RnRecyclerView', RnRecyclerView, {nativeOnly: {onChange:true}});
module.exports = RnRecyclerView;
