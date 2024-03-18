let
    nixpkgs = fetchTarball "https://github.com/NixOS/nixpkgs/archive/9957cd48326fe8dbd52fdc50dd2502307f188b0d.tar.gz";
    pkgs = import nixpkgs { config.permittedInsecurePackages = [ "nodejs-16.20.2" ]; };
in
pkgs.mkShell {
    packages = with pkgs; [
        yarn
        python38
        nodejs_16
        pkg-config
        glib
        libsecret
    ];
}
