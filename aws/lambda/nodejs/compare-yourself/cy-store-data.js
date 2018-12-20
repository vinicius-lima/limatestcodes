const AWS = require("aws-sdk");
const dynamodb = new AWS.DynamoDB({
  region: "sa-east-1",
  apiVersion: "2012-08-10"
});

exports.handler = (event, context, callback) => {
  const params = {
    Item: {
      Userid: {
        S: event.userId
      },
      Age: {
        N: event.age
      },
      Height: {
        N: event.height
      },
      Income: {
        N: event.income
      }
    },
    TableName: "compare-yourself"
  };

  dynamodb.putItem(params, function(err, data) {
    if (err) {
      console.log(err);
      callback(err);
    } else {
      console.log(data);
      callback(null, data);
    }
  });
};
