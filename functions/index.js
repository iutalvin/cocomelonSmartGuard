const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

exports.sendNotificationAtScheduledTime = functions.pubsub.schedule('every 1 minutes')  // Test every minute
  .onRun(async (context) => {
    console.log('Triggered the scheduled function...');

    // Get user data (this assumes you have user data with alerts)
    const snapshot = await admin.database().ref('/users').once('value');
    const users = snapshot.val();

    if (!users) {
      console.log('No users found in the database.');
      return null;
    }

    // Loop through each user in the database
    for (const userId in users) {
      if (users.hasOwnProperty(userId)) {
        const user = users[userId];
        const alerts = user.alerts;

        // Check if there are any alerts to notify about
        if (alerts) {
          // Loop through each alert for the user
          for (const alertId in alerts) {
            const alert = alerts[alertId];

            // Prepare notification message
            const message = {
              notification: {
                title: 'Sensor Alert Triggered',
                body: `Sensor Alert: ${alert.alertType} - Value: ${alert.sensorValue}`,
              },
              data: {
                alertType: alert.alertType || 'unknown',
                sensorValue: alert.sensorValue.toString() || '0',
              },
              topic: `
