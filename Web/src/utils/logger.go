package utils

import "github.com/sirupsen/logrus"

var Logger *logrus.Logger = nil

func NewLogger(loglevel string) *logrus.Logger {
	logger := logrus.New()
	loggerLevel, err := logrus.ParseLevel(loglevel)
	if err != nil {
		loggerLevel = logrus.DebugLevel
	}
	logger.SetLevel(loggerLevel)

	return logger
}
