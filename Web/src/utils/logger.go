package utils

import "github.com/sirupsen/logrus"

var Logger *logrus.Logger = nil

func NewLogger(loglevel string) *logrus.Logger {
	formatter := new(logrus.TextFormatter)
	formatter.FullTimestamp = true

	logger := logrus.New()
	loggerLevel, err := logrus.ParseLevel(loglevel)
	if err != nil {
		loggerLevel = logrus.DebugLevel
	}
	logger.SetLevel(loggerLevel)
	logger.SetFormatter(formatter)

	return logger
}
