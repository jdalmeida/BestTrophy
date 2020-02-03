double computeGearPitch(double diameterAtCrown,double numberOfTeeth){
	return ((diameterAtCrown/2)*((360.0)/numberOfTeeth)*Math.PI/180)
}
// call a script from another library
def bevelGears = ScriptingEngine.gitScriptRun(
            "https://github.com/madhephaestus/GearGenerator.git", // git location of the library
            "bevelGear.groovy" , // file to load
            // Parameters passed to the funcetion
            [	  46,// Number of teeth gear a
	            24,// Number of teeth gear b
	            5,// thickness of gear A
	            6,// gear pitch in arc length mm
	           90,// shaft angle, can be from 0 to 100 degrees
	            0// helical angle, only used for 0 degree bevels
            ]
            )
//Print parameters returned by the script
println "Bevel gear axle center to center " + bevelGears.get(2) // radius of flat gear
println "Bevel gear axle Height " + bevelGears.get(3)	// radius of perp gear
println "Bevel angle " + bevelGears.get(4)
println "Bevel tooth face length " + bevelGears.get(5)
println "Gear B computed thickness " + bevelGears.get(6)
// return the CSG parts
           
File goatFile = ScriptingEngine.fileFromGit(
	"https://github.com/jdalmeida/BestTrophy.git",
	"goat.stl");


// Generate Gears
int numGears = 6
int numShafts = numGears/2

def roundGears=[];
def bolts=[]

double angle = 360/numGears

def boltsize = 30
def bolthead = 5
LengthParameter boltLength = new LengthParameter("Bolt Length",10,[180,10])
boltLength.setMM(boltsize)
CSG m5Bolt = Vitamins.get("capScrew", "M5").movez(boltsize).roty(90).movez(bevelGears.get(3)).movex(-1*bevelGears.get(2) + boltsize + bolthead)


// add gears around circle
for(int i=0; i<=numGears; i++){
	CSG gear = bevelGears.get(1).rotz(angle * i)
	
	bolts.add(Vitamins.get("capScrew", "M5").
		movez(boltsize).roty(90).
		movez(bevelGears.get(3)).movex(-1*bevelGears.get(2) + boltsize + 5).
		rotz(angle*i))

	roundGears.add(gear.difference(bolts.get(i)))
	roundGears.get(i).setName("Planet Gears")
}

def midBoltSize = 12
//LengthParameter boltLength = new LengthParameter("Bolt Length",10,[180,10])
boltLength.setMM(midBoltSize)

// Top Gear and Bolt
def topGear = bevelGears.get(0).roty(180).movez(2*bevelGears.get(3))
CSG topBolt = Vitamins.get("capScrew", "M5").movez(topGear.getMaxZ() - bolthead)
topGear = topGear.difference(topBolt)

// Bottom Gear and Bolt
def bottomBolt = Vitamins.get("capScrew", "M5").roty(180).movez(bolthead)
def bottomGear = bevelGears.get(0).difference(bottomBolt)

// Center nugget
def centerSize = 15
def center = new Dodecahedron(centerSize).toCSG().roty(60).movez(bevelGears.get(3))

center = center.difference(bottomBolt).difference(topBolt).difference(bolts)


// Goat Statue
CSG goat  = Vitamins.get(goatFile).scale(0.7).toZMin().movez(topGear.getMaxZ()-1).movex(-5)

// Notch in top Gear
topGear = topGear.difference(goat)

topGear.setName("top gear")
goat.setName('Goat')

// Make Motor
CSG motor = Vitamins.get("roundMotor", "WPI-gb37y3530-50en")

return [bottomGear, bottomBolt, topBolt, roundGears, bolts, center, topGear, goat]