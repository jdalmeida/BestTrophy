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

// Rotated Objects
def roundGears=[]
def bolts=[]
def bearings=[]
def inserts = []

double angle = 360/numGears

// Create M5 Bolts
def boltsize = 30
def bolthead = 5
LengthParameter boltLength = new LengthParameter("Bolt Length",10,[180,10])
boltLength.setMM(boltsize)
CSG m5Bolt = Vitamins.get("capScrew", "M5").toolOffset(0.5)

// Create bearing
CSG bearingTemp = Vitamins.get("ballBearing", "695zz").toolOffset(0.5)
def bearingHeight = bearingTemp.getTotalZ()
bearingTemp = bearingTemp.roty(90)

// add gears around circle with bolts and bearings
for(int i=0; i<=numGears; i++){
	CSG gear = bevelGears.get(1).rotz(angle * i)
	
	bolts.add(m5Bolt.movez(boltsize).
		roty(90).
		movez(bevelGears.get(3)).
		movex(-1*bevelGears.get(2) + boltsize).
		rotz(angle*i))
		
	bearings.add(bearingTemp.movez(bevelGears.get(3))
		.movex(-1*bevelGears.get(2) + bearingHeight)
		.rotz(angle*i))
	
	roundGears.add(gear.difference(bearings.get(i)).
		difference(bolts.get(i)))
		
	roundGears.get(i).setName("Planet Gears")
}


// Center knuckle
def centerSize = 20
def center = new Cylinder(centerSize, centerSize, centerSize, numGears).toCSG().
	rotz(30).
	movez(bevelGears.get(3)-centerSize/2)


// Create nut inserts
CSG insertTemp = Vitamins.get("heatedThreadedInsert", "M5").roty(-90).
	movex(center.getMinX()).
	movez(bevelGears.get(3))

for(int i=0; i<=numGears; i++){
	inserts.add(insertTemp.rotz(angle*i))
}

// 12mm Bolt
def smallBoltSize = 12
LengthParameter smallBoltLength = new LengthParameter("Bolt Length",10,[180,10])
smallBoltLength.setMM(smallBoltSize)
def m5Bolt12 = Vitamins.get("capScrew", "M5").toolOffset(0.5)

topandbottom = []

// Top Gear and Cuts
def topGear = bevelGears.get(0).
	roty(180).
	movez(2*bevelGears.get(3))
def topBolt = m5Bolt12.
	movez(topGear.getMaxZ())
def topBearing = bearingTemp.
	roty(-90).
	movez(2*bevelGears.get(3) - bearingHeight)
topGear = topGear.difference(topBolt).
	difference(topBearing)

topandbottom.add([topGear, topBolt, topBearing])

// Bottom Gear and Cuts
def bottomGear = bevelGears.get(0)
def bottomBolt = m5Bolt12.roty(180).movez(bolthead)
def bottomBearing = bearingTemp.roty(-90)
bottomGear = bottomGear.difference(bottomBolt).
	difference(bottomBolt)

topandbottom.add([bottomGear, bottomBolt, bottomBearing])

// Make Motor
CSG motor = Vitamins.get("roundMotor", "WPI-gb37y3530-50en")
//bottomGear = bottomGear.difference(motor)

// Center Shaft Holes
center = center.difference(bottomBolt).
	difference(topBolt).
	difference(bolts).
	difference(inserts)
//	.difference(motor)

	
// Goat Statue
CSG goat  = Vitamins.get(goatFile).
	scale(0.7).
	toZMin().
	movez(topGear.getMaxZ()-1).
	movex(-5)

// Notch in top Gear
//topGear = topGear.minkowskiDifference(goat, 0.5)

// Name Things
topGear.setName("Top Gear")
bottomGear = bottomGear.setName("Bottom Gear")
goat.setName("Goat")
center.setName("Center")

return[topandbottom, roundGears, center, bolts, bearings]
//return [bottomGear, topBolt, roundGears, bolts, center, topGear]
//return [center] 