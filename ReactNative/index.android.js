/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  Image,
  NativeModules,
  DeviceEventEmitter,
  RefreshControl,
  PixelRatio
} from 'react-native';

var ReactNative = require('ReactNative');
import RnRecycleView from './RecyclerList/RnRecyclerView.js';


var dataSet = [];


var rowMap={"0":[0],"1":[1]}; //view类型，一共两种，第0种类型对应第0个view，第一种类型对应第1个view


class RecyclerList extends Component {
    constructor(props) {
        super(props);
         this.state = {isRefreshing:false};
        this._itemClicked = this._itemClicked.bind(this);  
       DeviceEventEmitter.addListener(
                       'item_clicked',
                       this._itemClicked);
        this._onRefresh = this._onRefresh.bind(this);
        this._endReached = this._endReached.bind(this);
       
    }
    
     _itemClicked(e:Event){
        alert("click:"+e.position);
    }
    
    _endReached(){
        alert("endReached add 10 rows data");
        var extData = [];
        
        for(ii=0; ii<10; ii++){
           if(ii%2 === 0){
               //使用0类型的view
               extData.push({'viewType':0, 'img':'http://facebook.github.io/react/img/logo_og.png', 'data':'ext data row'+ii});
           }else{
               //使用1类型的view， 其中data对应的TextView还支持html标签哦。
               extData.push({'viewType':1, 'img':'http://facebook.github.io/react/img/logo_og.png', 'data':"<font size=16 color='#ff0000'>ext data row"+ii+"</font>"});
           }
           
       }
       
       //给RecyclerView添加数据, 命令3表示添加数据
       NativeModules.UIManager.dispatchViewManagerCommand( ReactNative.findNodeHandle(this.refs.recycle), 3, extData);
    }
    
    _onRefresh(){
        
        this.setState({isRefreshing: true});
      
        setTimeout(function(){
            isRefreshing = false;
            this.setState({isRefreshing: false});
        }.bind(this), 3000);
    }

    componentDidMount(){
        //构造数据
       for(ii=0; ii<100; ii++){
           if(ii%2 === 0){
               //使用0类型的view
               dataSet.push({'viewType':0, 'img':'http://facebook.github.io/react/img/logo_og.png', 'data':'row'+ii});
           }else{
               //使用1类型的view， 其中data对应的TextView还支持html标签哦。
               dataSet.push({'viewType':1, 'img':'http://facebook.github.io/react/img/logo_og.png', 'data':"<font size=16 color='#ff0000'>row"+ii+"</font>"});
           }
           
       }
       //设置数据给RecyclerView, 命令2表示设置数据
       NativeModules.UIManager.dispatchViewManagerCommand( ReactNative.findNodeHandle(this.refs.recycle), 2, dataSet);
       //同时滚动到第5行， 命令1表示滚动到某一行
       NativeModules.UIManager.dispatchViewManagerCommand( ReactNative.findNodeHandle(this.refs.recycle), 1, [5]);
    }


    _renderRow(index) {
        //创建type为0的view, 图片在左
        if(index == 0){
            return (
       
                  <View style={{backgroundColor:'#ffff00', flexDirection: 'row', alignItems: 'center', paddingLeft: 10, paddingRight: 10}}>
                       <Image testID='img' source={{uri:'ic_launcher'}} style={{width:70, height:70}}  resizeMode={Image.resizeMode.stretch}/>
                       <Text testID='data' style={{color:'#555555', fontSize:14, marginLeft:10}}>
                             aaaaaaaaaa
                       </Text>
                  </View>
                );
        }else if(index == 1){
            //创建viewType为1的view， 图片在右
           return (
       
                  <View style={{backgroundColor:'#00ffff', flexDirection: 'row', alignItems: 'center', paddingLeft: 10, paddingRight: 10}}>
                        <Text testID='data' style={{color:'#555555', fontSize:14, flex:1}}>
                             aaaaaaaaaa
                       </Text>
                       <Image testID='img' source={{uri:'ic_launcher'}} style={{width:70, height:70}}  resizeMode={Image.resizeMode.stretch}/>
                  </View>
                );
        }

    
   }

    render(){
        return (<RefreshControl style={{flex:1}}
                    refreshing={this.state.isRefreshing}
                    onRefresh={this._onRefresh}>
                <RnRecycleView ref='recycle'
                style={{flex:1, paddingTop:2}}
                childViewCount={2} // 一共创建两行view
                renderTypeView={this._renderRow} //创建view的函数
                viewTypesMap={rowMap} //view类型映射关系
                endReached={this._endReached}
        >
        </RnRecycleView>
        </RefreshControl>);
    }
}



AppRegistry.registerComponent('test', () => RecyclerList);
