double computeGearPitch(double diameterAtCrown,double numberOfTeeth){
	return ((diameterAtCrown/2)*((360.0)/numberOfTeeth)*Math.PI/180)
}
// call a script from another library
def bevelGears = ScriptingEngine.gitScriptRun(
            "https://github.com/madhephaestus/GearGenerator.git", // git location of the library
            "bevelGear.groovy" , // file to load
            // Parameters passed to the funcetion
            [	  34,// Number of teeth gear a
	            24,// Number of teeth gear b
	            6,// thickness of gear A
	            computeGearPitch(26.15,24),// gear pitch in arc length mm
	           90,// shaft angle, can be from 0 to 100 degrees
	            0// helical angle, only used for 0 degree bevels
            ]
            )
//Print parameters returned by the script
println "Bevel gear axle center to center " + bevelGears.get(2)
println "Bevel gear axle Height " + bevelGears.get(3)
println "Bevel angle " + bevelGears.get(4)
println "Bevel tooth face length " + bevelGears.get(5)
println "Gear B computed thickness " + bevelGears.get(6)
// return the CSG parts
bevelgears roundGears[];

int numGears = 3;

for(i=0, i<=numGears, i++){
	roundGears[i] = bevelGears.get(1).rotz(90 * i);
}

def topGear = bevelGears.get(0).roty(180).movez(2*bevelGears.get(3));

return [bevelGears.get(0), topGear]
