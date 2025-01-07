exports.sendSensorNotification = functions.database
  .ref('/users/{uid}/alerts/{timestamp}')
  .onCreate(async (snapshot, context) => {
    console.log('Context params:', context.params);  // Log context.params to verify

    const alertData = snapshot.val();
    const uid = context.params.uid;
    const timestamp = context.params.timestamp;

    if (!alertData) {
      console.log('No alert data found');
      return null;
    }

    const message = {
      notification: {
        title: 'Sensor Alert',
        body: `New alert at ${timestamp}: ${alertData.alertType} - ${alertData.sensorValue}`,
      },
      topic: uid,
    };

    try {
      const response = await admin.messaging().send(message);
      console.log('Notification sent successfully:', response);
      return null;
    } catch (error) {
      console.error('Error sending notification:', error);
      return null;
    }
  });
