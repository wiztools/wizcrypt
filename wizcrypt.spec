##########################################################################
# Need To Have JAVA_HOME environment variable set, And Maven >= 2.0.3 in #
# the PATH.                                                              #
##########################################################################

%define name wizcrypt
%define version 2.2
%define release 1

Summary: Cross platform file encryption software.
Name: %{name}
Version: %{version}
Release: %{release}
Source: http://prdownload.berlios.de/wizcrypt/%{name}-%{version}-src.tar.bz2
Vendor: WizTools.org
URL: http://www.wiztools.org/
License: Apache 2
Group: System Environment/Libraries
BuildArch: noarch
Prefix: %{_prefix}

%description
Cross platform file encryption software.

%prep
%setup -q

%build
mvn compile

%install
if mvn assembly:assembly -DdescriptorId=jar-with-dependencies
then
	# Copy Binaries
	if [ ! -d ${RPM_BUILD_ROOT}/usr/share/java ];then
		mkdir -p ${RPM_BUILD_ROOT}/usr/share/java
	fi
	if [ ! -d ${RPM_BUILD_ROOT}/usr/bin ];then
		mkdir -p ${RPM_BUILD_ROOT}/usr/bin
	fi
	cp target/%{name}-%{version}-jar-with-dependencies.jar ${RPM_BUILD_ROOT}/usr/share/java/
	cp src/main/shell/wizcrypt ${RPM_BUILD_ROOT}/usr/bin/wizcrypt
	chmod +x ${RPM_BUILD_ROOT}/usr/bin/wizcrypt

	# Copy man page
	if [ ! -d ${RPM_BUILD_ROOT}/usr/share/man/man1 ];then
		mkdir -p ${RPM_BUILD_ROOT}/usr/share/man/man1
	fi
	cp src/main/doc/wizcrypt.man ${RPM_BUILD_ROOT}/usr/share/man/man1/wizcrypt.1
	gzip -f ${RPM_BUILD_ROOT}/usr/share/man/man1/wizcrypt.1

	# Generate Documentation
	mvn site
fi

%clean
# Do nothing

%files
%defattr(-,root,root)
%doc /usr/share/man/man1/wizcrypt.1.gz
%doc target/site/*
/usr/share/java/%{name}-%{version}-jar-with-dependencies.jar
/usr/bin/wizcrypt

