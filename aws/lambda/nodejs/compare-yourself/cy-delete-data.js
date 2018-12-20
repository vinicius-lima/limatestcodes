const AWS = require("aws-sdk");
const dynamodb = new AWS.DynamoDB({
  region: "sa-east-1",
  apiVersion: "2012-08-10"
});

exports.handler = (event, context, callback) => {
  const params = {
    Key: {
      Userid: {
        S: event.userId
      }
    },
    TableName: "compare-yourself"
  };

  dynamodb.deleteItem(params, function(err, data) {
    if (err) {
      console.log(err);
      callback(err);
    } else {
      console.log(data);
      callback(null, data);
    }
  });
};
