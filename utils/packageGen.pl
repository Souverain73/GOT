use strict;

open(my $inputFile, "<", "packages.lst") or die "Can't open packages list";

my $modifier = 'broadcast';
ml: while (my $line = <$inputFile>){
	if ($line=~ m/(.*):/){
		$modifier = $1;
		next ml;
	}
	if (!$line =~ m/([a-zA-Z\S]*)\((.*)\) ?-? ?(.*)?/){
		next ml;
	} 
	$line =~ m/([a-zA-Z\S]*)\((.*)\) ?-? ?(.*)?/;
	my ($name, $args, $comment) = ($1, $2, $3);
	my @args = split(',', $args);
	
	&printHeader($modifier, ucfirst $name, $comment);
	
	foreach my $arg(@args){
		$arg =~ m/(.*?)(\[(\d*)\])?$/;
		&printField($1);
	}

	&printConstruct(ucfirst $name, \@args);

	&printEnd();
}

sub printHeader{
	my ($mod, $name, $comment) = @_;
	$comment = ucfirst $comment;
	my $baseClass = "Package";

	if ($mod eq "broadcast")	{$baseClass = "BroadcastPackage";} 
	if ($mod eq "client")		{$baseClass = "ServerClientPackage";}
	if ($mod eq "server")		{$baseClass = "ClientServerPackage";}
	

	print qq(/**\n* $comment\n*/
public static class $name extends $baseClass {\n);

}

sub printEnd{
	print qq(}\n\n);
}

sub printField{
	my ($name) = @_;
	print qq(int $name;\n);
}

sub printConstruct{
	my ($className, $argsRef) = @_;
	my @args = @{$argsRef};
	print qq(public $className() {\n);

	foreach my $arg(@args){
		$arg =~ m/(.*?)(\[(\d*)\])?$/;
		if (defined $3){
			print qq($1 = new int[$3];\n);
		}
	}

	print qq(}\n);
}