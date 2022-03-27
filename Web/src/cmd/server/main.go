package main

import (
	. "bgf/configs"
	"bgf/internal/app/apiserver"
	"flag"
	"log"

	"github.com/BurntSushi/toml"
)

var (
	configPath string
)

func init() {
	flag.StringVar(&configPath, "config-path", "configs/server.toml", "path to config file")
}

func main() {
	// Make config
	config, err := makeConfig()
	if err != nil {
		log.Fatal(err)
	}

	// Update global config
	ServerConfig = config

	if err := apiserver.Start(); err != nil {
		log.Fatal(err)
	}
}

func makeConfig() (*Config, error) {
	flag.Parse()
	config := NewConfig()
	_, err := toml.DecodeFile(configPath, config)
	return config, err
}
