import React from 'react';
import { StyleSheet, Button, View, SafeAreaView, Text, Alert, NativeModules } from 'react-native';

const Separator = () => (
  <View style={styles.separator} />
);

const Space = () => (
  <View style={styles.space} />
);

export default class App extends React.Component {
  constructor() {
    super();
    this.state = { resultText: 'Initial Text' };
  }

  onSetText(text) {
    this.setState({
      resultText: text,
    })
  };

  render() {
    return (
      <SafeAreaView style={styles.container}>
        <Space />
        <View>
          <Button
            title="Get Device Info"
            onPress={() => {
              NativeModules.MorefunReactModule.getDeviceInfo().then((data) => {
                this.onSetText(data)
              }, (error) => {
                this.onSetText(data)
              });
            }}
          />
          <Space />
          <Button
            title="Read Mag Card"
            onPress={() => {
              this.onSetText("Please swipe mag card")
              NativeModules.MorefunReactModule.readMagCard().then((data) => {
                this.onSetText(data)
              }, (error) => {
                this.onSetText(data)
              });
            }}
          />
          <Space />
          <Button
            title="Print"
            onPress={() => {
              var printData = new Array()
              printData.push("MERCHANT NAME：Demo shop name");
              printData.push("MERCHANT NO.：20321545656687");
              printData.push("TERMINAL NO.：25689753");
              printData.push("CARD NUMBER");
              printData.push("62179390*****3426");
              printData.push("TRANS TYPE");
              printData.push("SALE");
              printData.push("EXP DATE：2029");
              printData.push("BATCH NO：000012");
              printData.push("VOUCHER NO：000001");
              printData.push("DATE/TIME：2016-05-23 16:50:32");
              printData.push("AMOUNT");
              printData.push("--------------------------------------");
              printData.push(" I ACKNOWLEDGE	SATISFACTORY RECEIPT OF RELATIVE GOODS/SERVICES");
              printData.push(" MERCHANT COPY ");
              printData.push("---X---X---X---X---X--X--X--X--X--X--\n");
              printData.push("\n");

              this.onSetText("Printing...")
              NativeModules.MorefunReactModule.print(printData).then((data) => {
                this.onSetText(data)
              }, (error) => {
                this.onSetText(data)
              });
            }}
          />
        </View>
        <Separator />
        <Text style={styles.title}>
          {this.state.resultText}
        </Text>
      </SafeAreaView>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'flex-start',
    marginHorizontal: 16,
  },
  title: {
    textAlign: 'center',
    marginVertical: 8,
  },
  fixToText: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  separator: {
    marginVertical: 8,
    borderBottomColor: '#737373',
    borderBottomWidth: StyleSheet.hairlineWidth,
  },
  resultText: {
    color: 'white',
    fontSize: 12,
    alignSelf: 'flex-start',
  },
  space: {
    marginVertical: 5,
  },
});

